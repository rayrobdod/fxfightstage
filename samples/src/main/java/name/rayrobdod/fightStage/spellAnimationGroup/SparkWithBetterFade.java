package name.rayrobdod.fightStage.spellAnimationGroup;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * @see <a href="https://www.patreon.com/posts/electricity-7075305">Medeiros electricity animation tutorial</a>
 */
public final class SparkWithBetterFade implements SpellAnimationGroup {
	
	private static final Duration flickerRate = Duration.seconds(1f / 12f);
	
	private static final double segmentMaxParDelta = 50;
	private static final double segmentMinParDelta = 20;
	private static final double segmentMaxPerpValue = 40;
	private static final double segmentMinPerpValue = -10;
	private static final double sharpLineWidth = 3;
	private static final double blurredLineWidth = 12;
	private static final Color sharpColor = Color.hsb(240, 0.15, 0.95, 0.9);
	private static final Color sharpColorBright = Color.hsb(260, 0.05, 0.95);
	private static final Color blurredColor = Color.hsb(250, 0.1, 0.85, 0.7);
	private static final Color blurredColorBright = Color.hsb(270, 0.05, 0.85, 0.7);
	
	private final Node background;
	private final Node foreground;
	private final Path sharpShape;
	private final Path blurredShape;
	private final WritableValue<List<PathElement>> sharpShapePath;
	private final WritableValue<List<PathElement>> blurredShapePath;
	
	public SparkWithBetterFade() {
		this.sharpShape = new Path();
		this.blurredShape = new Path();
		
		this.sharpShape.setStroke(Color.TRANSPARENT);
		this.sharpShape.setEffect(new GaussianBlur(1));
		this.blurredShape.setStroke(Color.TRANSPARENT);
		this.blurredShape.setEffect(new GaussianBlur(6));
		this.blurredShape.setBlendMode(BlendMode.SCREEN);
		
		this.sharpShapePath = new WritableObservableListWrapper<>(
				sharpShape.getElements());
		this.blurredShapePath = new WritableObservableListWrapper<>(
				blurredShape.getElements());
		
		this.background = new Group();
		this.foreground = new Group(
			this.sharpShape,
			this.blurredShape
		);
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final Random rng = new Random();
		final List<Point2D> line1 = generateLightningLine(origin, target);
		final List<Point2D> line2 = generateLightningLine(origin, target);
		final List<SparkCornerPathElems> sharpParts1 = lightningLineToPathElems(line1, sharpLineWidth);
		final List<SparkCornerPathElems> sharpParts2 = lightningLineToPathElems(line2, sharpLineWidth);
		final List<SparkCornerPathElems> blurredParts1 = lightningLineToPathElems(line1, blurredLineWidth);
		final List<SparkCornerPathElems> blurredParts2 = lightningLineToPathElems(line2, blurredLineWidth);
		final List<PathElement> emptyElems = emptyList();
		final List<PathElement> sharpPathElems1 = (
			sharpParts1.stream().flatMap(x -> x.elementsStream()).collect(Collectors.toList())
		);
		final List<PathElement> sharpPathElems2 = (
			sharpParts2.stream().flatMap(x -> x.elementsStream()).collect(Collectors.toList())
		);
		final List<PathElement> blurredPathElems1 = (
			blurredParts1.stream().flatMap(x -> x.elementsStream()).collect(Collectors.toList())
		);
		final List<PathElement> blurredPathElems2 = (
			blurredParts2.stream().flatMap(x -> x.elementsStream()).collect(Collectors.toList())
		);
		
		final Timeline timeline = new Timeline();
		
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(sharpShapePath, sharpPathElems1, Interpolator.DISCRETE),
			new KeyValue(sharpShape.fillProperty(), sharpColor, Interpolator.DISCRETE),
			new KeyValue(blurredShapePath, blurredPathElems1, Interpolator.DISCRETE),
			new KeyValue(blurredShape.fillProperty(), blurredColor, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate,
			new KeyValue(sharpShapePath, sharpPathElems2, Interpolator.DISCRETE),
			new KeyValue(sharpShape.fillProperty(), sharpColorBright, Interpolator.DISCRETE),
			new KeyValue(blurredShapePath, blurredPathElems2, Interpolator.DISCRETE),
			new KeyValue(blurredShape.fillProperty(), blurredColorBright, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(2),
			new KeyValue(sharpShapePath, emptyElems, Interpolator.DISCRETE),
			new KeyValue(sharpShape.fillProperty(), sharpColor, Interpolator.DISCRETE),
			new KeyValue(blurredShapePath, emptyElems, Interpolator.DISCRETE),
			new KeyValue(blurredShape.fillProperty(), blurredColor, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(3),
			new KeyValue(sharpShapePath, sharpPathElems1, Interpolator.DISCRETE),
			new KeyValue(sharpShape.fillProperty(), sharpColor, Interpolator.DISCRETE),
			new KeyValue(blurredShapePath, blurredPathElems1, Interpolator.DISCRETE),
			new KeyValue(blurredShape.fillProperty(), blurredColor, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(4),
			new KeyValue(sharpShapePath, emptyElems, Interpolator.DISCRETE),
			new KeyValue(sharpShape.fillProperty(), sharpColor, Interpolator.DISCRETE),
			new KeyValue(blurredShapePath, emptyElems, Interpolator.DISCRETE),
			new KeyValue(blurredShape.fillProperty(), blurredColor, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(5),
			new KeyValue(sharpShapePath, sharpPathElems1, Interpolator.DISCRETE),
			new KeyValue(sharpShape.fillProperty(), sharpColor, Interpolator.DISCRETE),
			new KeyValue(blurredShapePath, blurredPathElems1, Interpolator.DISCRETE),
			new KeyValue(blurredShape.fillProperty(), blurredColor, Interpolator.DISCRETE)
		));
		
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			sharpParts1.stream().flatMap(x -> x.startValues().stream()).map(x -> x.toKeyValue(Interpolator.DISCRETE)).toArray(KeyValue[]::new)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(2),
			sharpParts1.stream().flatMap(x -> x.startValues().stream()).map(x -> x.toKeyValue(Interpolator.DISCRETE)).toArray(KeyValue[]::new)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(8),
			sharpParts1.stream().flatMap(x -> x.endValues().stream()).map(x -> x.toKeyValue(Interpolator.LINEAR)).toArray(KeyValue[]::new)
		));
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			blurredParts1.stream().flatMap(x -> x.startValues().stream()).map(x -> x.toKeyValue(Interpolator.DISCRETE)).toArray(KeyValue[]::new)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(2),
			blurredParts1.stream().flatMap(x -> x.startValues().stream()).map(x -> x.toKeyValue(Interpolator.DISCRETE)).toArray(KeyValue[]::new)
		));
		timeline.getKeyFrames().add(new KeyFrame(flickerRate.multiply(8),
			blurredParts1.stream().flatMap(x -> x.endValues().stream()).map(x -> x.toKeyValue(Interpolator.LINEAR)).toArray(KeyValue[]::new)
		));

		
		return new ParallelTransition(
			panAnimation,
			timeline,
			new SequentialTransition(
				new PauseTransition(flickerRate.multiply(2)),
				hpAndShakeAnimation
			)
		);
	}
	
	/** Creates a jagged line, and then returns the points that form that line */
	private static List<Point2D> generateLightningLine(final Point2D origin, final Point2D target) {
		final Random rng = new Random();
		final List<Point2D> retval = new ArrayList<>();
		
		final double distance = origin.distance(target);
		final Point2D parUnitVector = target.subtract(origin).normalize();
		final Point2D perpUnitVector = new Point2D(-parUnitVector.getY(), parUnitVector.getX()).normalize();
		
		retval.add(origin);
		
		double currentParallel = parDelta(rng, distance);
		double perpPolarity = (rng.nextBoolean() ? 1.0 : -1.0);
		while (currentParallel < 1.0) {
			final double perp = perpPolarity * (segmentMinPerpValue + (segmentMaxPerpValue - segmentMinPerpValue) * rng.nextDouble());
			
			retval.add(origin.add(parUnitVector.multiply(distance * currentParallel)).add(perpUnitVector.multiply(perp)));
			
			currentParallel += parDelta(rng, distance);
			perpPolarity *= -1.0;
		}
		
		retval.add(target);
		
		return retval;
	}
	
	private static List<SparkCornerPathElems> lightningLineToPathElems(List<Point2D> points, double lineWidth) {
		final List<SparkCornerPathElems> retval = new ArrayList<>(points.size() - 2);
		for (int i = 1; i < points.size() - 1; i++) {
			retval.add(new SparkCornerPathElems(
				points.get(i),
				points.get(i - 1),
				points.get(i + 1),
				lineWidth
			));
		}
		return retval;
	}
	
	private static double parDelta(Random rng, double distance) {
		return (segmentMinParDelta + (segmentMaxParDelta - segmentMinParDelta) * rng.nextDouble()) / distance;
	}
	
	/**
	 * A pair consisting of a WritableValue and a value that can
	 * be written to the WritableValue.
	 */
	public static final class WritableValuePair<T> {
		private final WritableValue<T> writable;
		private final T value;
		
		public WritableValuePair(
			WritableValue<T> writable,
			T value
		) {
			this.writable = writable;
			this.value = value;
		}
		
		/**
		 * Sets the WritableValue's value to the value given in this's constructor
		 */
		public void apply() { writable.setValue(value); }
		
		public KeyValue toKeyValue(Interpolator interp) {
			return new KeyValue(writable, value, interp);
		}
	}
	
	/**
	 * A WritableValue which sets and gets the values from a given ObservableList.
	 * <p>
	 * Mostly intended for KeyValue animations. Especially since attempts to use
	 * `ListProperty` failed, especially with multiple ListProperties in the same
	 * animation, or was unduly cumbersome.
	 */
	public static final class WritableObservableListWrapper<T> implements WritableValue<List<T>> {
		private final ObservableList<T> backing;
		
		public WritableObservableListWrapper(
			ObservableList<T> backing
		) {
			this.backing = backing;
		}
		
		/**
		 * Clears the backing list, then copies elements from the parameter to the
		 * backing list.
		 * @see ObservableList#setAll
		 */
		public void setValue(List<T> vals) {
			backing.setAll(vals);
		}
		
		public List<T> getValue() {
			return java.util.Collections.unmodifiableList(new ArrayList<>(backing));
		}
	}
	
	/**
	 * The spark path is split into sections around each corner, such that one
	 * shape consists of the midpoint of one line segment to the midpoint of the
	 * next line segment. This represents one of those segments.
	 */
	private final static class SparkCornerPathElems {
		private final Point2D corner;
		private final Point2D control1;
		private final Point2D control2;
		private final double lineWidth;
		
		private final MoveTo cornerA;
		private final LineTo control1A;
		private final LineTo control1B;
		private final LineTo cornerB;
		private final LineTo control2A;
		private final LineTo control2B;
		
		private final Point2D control1Midpoint;
		private final Point2D control2Midpoint;
		private final Point2D control1ParellelUnitVector;
		private final Point2D control2ParellelUnitVector;
		private final Point2D control1PerpendicularUnitVector;
		private final Point2D control2PerpendicularUnitVector;
		private final Point2D control1AInit;
		private final Point2D control1BInit;
		private final Point2D control2AInit;
		private final Point2D control2BInit;
		private final Point2D cornerAInit;
		private final Point2D cornerBInit;
		
		public SparkCornerPathElems(
			Point2D corner,
			Point2D control1,
			Point2D control2,
			double lineWidth
		) {
			this.corner = corner;
			this.control1 = control1;
			this.control2 = control2;
			this.lineWidth = lineWidth;
			
			this.cornerA = new MoveTo();
			this.control1A = new LineTo();
			this.control1B = new LineTo();
			this.cornerB = new LineTo();
			this.control2B = new LineTo();
			this.control2A = new LineTo();
			
			
			this.control1Midpoint = corner.midpoint(control1);
			this.control2Midpoint = corner.midpoint(control2);
			
			this.control1ParellelUnitVector = control1.subtract(corner).normalize();
			this.control2ParellelUnitVector = control2.subtract(corner).normalize();
			this.control1PerpendicularUnitVector = new Point2D(control1ParellelUnitVector.getY(), -control1ParellelUnitVector.getX()).normalize();
			this.control2PerpendicularUnitVector = new Point2D(control2ParellelUnitVector.getY(), -control2ParellelUnitVector.getX()).normalize();
			
			this.control1AInit = control1Midpoint.add(control1PerpendicularUnitVector.multiply(lineWidth / 2));
			this.control1BInit = control1Midpoint.add(control1PerpendicularUnitVector.multiply(-lineWidth / 2));
			this.control2AInit = control2Midpoint.add(control2PerpendicularUnitVector.multiply(-lineWidth / 2));
			this.control2BInit = control2Midpoint.add(control2PerpendicularUnitVector.multiply(lineWidth / 2));
			this.cornerAInit = intersection(control1AInit, control1ParellelUnitVector, control2AInit, control2ParellelUnitVector);
			this.cornerBInit = intersection(control1BInit, control1ParellelUnitVector, control2BInit, control2ParellelUnitVector);
			
			this.startValues().forEach(WritableValuePair::apply);
		}
		
		/**
		 * The path elements, in order, of this segment. The stream
		 * elements are identity-equal. The stream is not.
		 */
		public Stream<PathElement> elementsStream() {
			return Stream.of(
				  this.cornerA
				, this.control1A
				, this.control1B
				, this.cornerB
				, this.control2B
				, this.control2A
			);
		}
		
		private static Point2D intersection(Point2D line1Point, Point2D line1Direction, Point2D line2Point, Point2D line2Direction) {
			final double m1 = line1Direction.getY() / line1Direction.getX();
			final double b1 = line1Point.getY() - m1 * line1Point.getX();
			final double m2 = line2Direction.getY() / line2Direction.getX();
			final double b2 = line2Point.getY() - m2 * line2Point.getX();
			
			if (m1 == m2 || Double.isInfinite(m1) && Double.isInfinite(m2)) {
				// In this case, lines are either coincident or parallel, but I don't want to throw an exception
				// and an unlikely visual glitch is probably the worst that will happen if this does something simple
				return line1Point.midpoint(line2Point);
			} else if (Double.isInfinite(m1)) {
				return new Point2D(line1Point.getX(), m2 * line1Point.getX() + b2);
			} else if (Double.isInfinite(m2)) {
				return new Point2D(line2Point.getX(), m1 * line2Point.getX() + b1);
			} else {
				final double x = (b2 - b1) / (m1 - m2);
				final double y = m1 * x + b1;
				return new Point2D(x, y);
			}
		}
		
		public List<WritableValuePair<Number>> startValues() {
			return java.util.Arrays.asList(
				new WritableValuePair<>(this.control1A.xProperty(), control1AInit.getX()),
				new WritableValuePair<>(this.control1A.yProperty(), control1AInit.getY()),
				new WritableValuePair<>(this.control1B.xProperty(), control1BInit.getX()),
				new WritableValuePair<>(this.control1B.yProperty(), control1BInit.getY()),
				new WritableValuePair<>(this.control2A.xProperty(), control2AInit.getX()),
				new WritableValuePair<>(this.control2A.yProperty(), control2AInit.getY()),
				new WritableValuePair<>(this.control2B.xProperty(), control2BInit.getX()),
				new WritableValuePair<>(this.control2B.yProperty(), control2BInit.getY()),
				new WritableValuePair<>(this.cornerA.xProperty(), cornerAInit.getX()),
				new WritableValuePair<>(this.cornerA.yProperty(), cornerAInit.getY()),
				new WritableValuePair<>(this.cornerB.xProperty(), cornerBInit.getX()),
				new WritableValuePair<>(this.cornerB.yProperty(), cornerBInit.getY())
			);
		}
		
		public List<WritableValuePair<Number>> endValues() {
			return java.util.Arrays.asList(
				new WritableValuePair<>(this.control1A.xProperty(), corner.getX()),
				new WritableValuePair<>(this.control1A.yProperty(), corner.getY()),
				new WritableValuePair<>(this.control1B.xProperty(), corner.getX()),
				new WritableValuePair<>(this.control1B.yProperty(), corner.getY()),
				new WritableValuePair<>(this.control2A.xProperty(), corner.getX()),
				new WritableValuePair<>(this.control2A.yProperty(), corner.getY()),
				new WritableValuePair<>(this.control2B.xProperty(), corner.getX()),
				new WritableValuePair<>(this.control2B.yProperty(), corner.getY()),
				new WritableValuePair<>(this.cornerA.xProperty(), corner.getX()),
				new WritableValuePair<>(this.cornerA.yProperty(), corner.getY()),
				new WritableValuePair<>(this.cornerB.xProperty(), corner.getX()),
				new WritableValuePair<>(this.cornerB.yProperty(), corner.getY())
			);
		}
		
	}
}
