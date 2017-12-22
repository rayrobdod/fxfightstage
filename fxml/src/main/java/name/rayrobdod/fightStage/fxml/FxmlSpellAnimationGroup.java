package name.rayrobdod.fightStage.fxml;

import java.util.List;

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
		
		/**
		 * @TODO support actual expressions, not just values.
		 */
		public static OffsetFunction valueOf(String spec) {
			switch(spec) {
				case "offsetX": return (ox, oy, tx, ty) -> ox;
				case "offsetY": return (ox, oy, tx, ty) -> oy;
				case "targetX": return (ox, oy, tx, ty) -> tx;
				case "targetY": return (ox, oy, tx, ty) -> ty;
				default: throw new IllegalArgumentException("OffsetFunction spec");
			}
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
