package name.rayrobdod.fightStage.fxml;

import java.util.List;
import java.util.function.DoubleBinaryOperator;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.CharIn;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.DelayedConstructionParser;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.End;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.IsString;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.ParseResult;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.Parser;
import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.Tuple2;

/**
 * An implementation of SpellAnimationGroup which can be more-easily constructed via fxml
 */
public final class FxmlSpellAnimationGroup implements SpellAnimationGroup {
	
	private final Node foreground;
	private final Node background;
	private final RelocatableTimeline anim;
	
	/**
	 * A class capable of create a Timeline where the KeyValue's endValue depends on run-time parameters
	 */
	public static final class RelocatableTimeline {
		private final List<KeyFrame> staticFrames;
		private final List<RelocatableKeyFrame> relocatableFrames;
		
		public RelocatableTimeline(
			  @NamedArg("staticFrames") List<KeyFrame> staticFrames
			, @NamedArg("relocatableFrames") List<RelocatableKeyFrame> relocatableFrames
		) {
			this.staticFrames = new java.util.ArrayList<>(staticFrames);
			this.relocatableFrames = new java.util.ArrayList<>(relocatableFrames);
		}
		
		public final Timeline realize(double ox, double oy, double tx, double ty) {
			return new Timeline(
				java.util.stream.Stream.<KeyFrame>concat(
					staticFrames.stream(),
					relocatableFrames.stream().<KeyFrame>map((a) -> a.realize(ox, oy, tx, ty))
				).toArray(KeyFrame[]::new)
			);
		}
	}
	
	/**
	 * A class capable of create a KeyFrame where the KeyValue's endValue depends on run-time parameters
	 */
	public static final class RelocatableKeyFrame {
		private final Duration time;
		private final List<RelocatableKeyValue> values;
		
		public RelocatableKeyFrame(
			  @NamedArg("time") Duration time
			, @NamedArg("values") List<RelocatableKeyValue> values
		) {
			this.time = time;
			this.values = new java.util.ArrayList<>(values);
		}
		
		public final KeyFrame realize(double ox, double oy, double tx, double ty) {
			return new KeyFrame(
				  time
				, values.stream().<KeyValue>map((a) -> a.realize(ox, oy, tx, ty)).toArray(KeyValue[]::new)
			);
		}
	}
	
	/**
	 * A class capable of create a KeyValue where the KeyValue's endValue depends on run-time parameters
	 */
	public static final class RelocatableKeyValue {
		private final DoubleProperty target;
		private final OffsetFunction endValue;
		private final Interpolator interpolator;
		
		public RelocatableKeyValue(
			  @NamedArg("target") DoubleProperty target
			, @NamedArg("endValue") OffsetFunction endValue
			, @NamedArg("interpolator") Interpolator interpolator
		) {
			this.target = target;
			this.endValue = endValue;
			this.interpolator = interpolator;
			
			if (null == this.endValue) {
				throw new NullPointerException("endValue");
			}
		}
		
		public final KeyValue realize(double ox, double oy, double tx, double ty) {
			final double endValue = this.endValue.apply(ox, oy, tx, ty);
			return new KeyValue(target, endValue, interpolator);
		}
	}
	
	@FunctionalInterface
	public interface OffsetFunction {
		public double apply(double originX, double originY, double targetX, double targetY);
		
		default OffsetFunction zipMap(OffsetFunction rhs, DoubleBinaryOperator combiner) {
			return (ox, oy, tx, ty) -> combiner.applyAsDouble(
				this.apply(ox, oy, tx, ty),
				rhs.apply(ox, oy, tx, ty)
			);
		}
		
		/**
		 */
		public static OffsetFunction valueOf(String spec) {
			ParseResult<OffsetFunction> res = MyParsers.valueOfParser().parse(spec);
			if (res.isSuccess) {
				return res.value;
			} else {
				String expectingString = (res.expecting.size() == 0 ? "???" :
					(res.expecting.size() == 1 ? res.expecting.get(0) :
						res.expecting.stream().collect(java.util.stream.Collectors.joining(", "))
					));
				
				throw new IllegalArgumentException("At " + res.charsRead + ": expecting one of <" + expectingString + ">");
			}
		}
		
		public static OffsetFunction constant(double value) { return (a,b,c,d) -> value; }
		public static OffsetFunction originX() { return (ox,oy,tx,ty) -> ox; }
		public static OffsetFunction originY() { return (ox,oy,tx,ty) -> oy; }
		public static OffsetFunction targetX() { return (ox,oy,tx,ty) -> tx; }
		public static OffsetFunction targetY() { return (ox,oy,tx,ty) -> ty; }
		
		public static class MyParsers {
			private MyParsers() {}
			private static final Parser<String> digits = new CharIn("0123456789").repAsString(1);
			private static final Parser<String> fracPart = (new CharIn(".").andThen(digits).map(x -> x.zip((a,b) -> a + b))).optionally("");
			private static final Parser<Double> number = digits.andThen(fracPart).map(x -> x.zip((a,b) -> a + b)).map(Double::parseDouble).opaque("A number");
			private static final Parser<OffsetFunction> constFun = number.map(OffsetFunction::constant);
			
			private static final Parser<OffsetFunction> variableFun = (
				new IsString("originX").map(x -> OffsetFunction.originX()).orElse(
				new IsString("originY").map(x -> OffsetFunction.originY()).orElse(
				new IsString("targetX").map(x -> OffsetFunction.targetX()).orElse(
				new IsString("targetY").map(x -> OffsetFunction.targetY())
			))));
			
			private static final Parser<OffsetFunction> parens() {return new DelayedConstructionParser<>(() ->
				(new IsString("(").andThen(plusMinus()).andThen(new IsString(")"))).map(x -> x._1._2)
			);}
			
			private static final Parser<OffsetFunction> factor() {return new DelayedConstructionParser<>(() ->
				constFun.orElse(variableFun).orElse(parens())
			);}
			
			private static final Parser<OffsetFunction> divMul() {return new DelayedConstructionParser<>(() ->
				factor().andThen( new CharIn("*/").andThen(factor()).rep() ).map(MyParsers::binaryOperatorMappingList)
			);}
			private static final Parser<OffsetFunction> plusMinus() {return new DelayedConstructionParser<>(() ->
				divMul().andThen( new CharIn("+-").andThen(divMul()).rep() ).map(MyParsers::binaryOperatorMappingList)
			);}
			
			private static final OffsetFunction binaryOperatorMappingList(Tuple2<OffsetFunction,List<Tuple2<Character,OffsetFunction>>> desc) {
				OffsetFunction left = desc._1;
				final List<Tuple2<Character,OffsetFunction>> opRightList = desc._2;
				for (Tuple2<Character,OffsetFunction> opRight : opRightList) {
					Character op = opRight._1;
					OffsetFunction right = opRight._2;
					left = binaryOperatorMapping(left, op, right);
				}
				return left;
			}
			private static final OffsetFunction binaryOperatorMapping(OffsetFunction left, Character operator, OffsetFunction right) {
				switch (operator) {
					case '+': return left.zipMap(right, (a,b) -> a + b);
					case '-': return left.zipMap(right, (a,b) -> a - b);
					case '*': return left.zipMap(right, (a,b) -> a * b);
					case '/': return left.zipMap(right, (a,b) -> a / b);
					case '%': return left.zipMap(right, (a,b) -> a % b);
					default: throw new IllegalArgumentException("binaryOperatorMapping operator: " + operator);
				}
			}
			
			
			public static final Parser<OffsetFunction> valueOfParser() {return plusMinus().andThen(new End()).map(x -> x._1);}
		}
	}
	
	public FxmlSpellAnimationGroup(
		  @NamedArg("foreground") Node foreground
		, @NamedArg("background") Node background
		, @NamedArg("anim") RelocatableTimeline anim
	) {
		this.foreground = foreground;
		this.background = background;
		this.anim = anim;
	}
	
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		return new SequentialTransition(
			panAnimation,
			this.anim.realize(origin.getX(), origin.getY(), target.getX(), target.getY()),
			hpAndShakeAnimation
		);
	}
	
	public String toString() {
		return "SpellAnimGroup[" + foreground + ", " + background + ", " + anim + "]";
	}
}
