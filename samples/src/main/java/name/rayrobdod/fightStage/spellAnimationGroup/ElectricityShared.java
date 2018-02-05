/*
 * Copyright 2018 Raymond Dodge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.rayrobdod.fightStage.spellAnimationGroup;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
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
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * A set of static methods that are shared among the electricity-based spells
 */
public final class ElectricityShared {
	private ElectricityShared() {}
	
	private static final double skyBoltMinY = -500;
	private static final double skyBoltDeltaXMax = 20;
	private static final double skyBoltDeltaXMin = -20;
	private static final double skyBoltDeltaYMax = 30;
	private static final double skyBoltDeltaYMin = 5;
	
	private static final double chainDeltaParMax = 30;
	private static final double chainDeltaParMin = 10;
	private static final double chainPerpMaxValue = 30;
	private static final double chainPerpMinValue = -10;
	
	/**
	 * Creates a jagged line which
	 * <ul>
	 * <li>starts at the point `(target.getX, groundY)`
	 * <li>each point has a smaller `y` than the preceding point (the line moves vertically upwards)
	 * <li>the point's `x` may drift, but drifts less so before the line reaches `target.getY`
	 * <li>Reaches as high as it needs to go offscreen
	 * </ul>
	 * 
	 * The generation does involve a {@link java.util.Random}, so this is not a functional function
	 */
	public static List<Point2D> skyBoltPoints(final Point2D origin, final Point2D target) {
		final double belowSpellTarget = 50;
		
		final Random rng = new Random();
		final List<Point2D> retval = new ArrayList<>();
		
		final double maxY = skyBoltMinY;
		double currentX = target.getX();
		double currentY = target.getY() + belowSpellTarget;
		do {
			retval.add(new Point2D(currentX, currentY));
			
			final double dx = (currentY >= target.getY() ? 0.25 : 1) * (skyBoltDeltaXMin + (skyBoltDeltaXMax - skyBoltDeltaXMin) * rng.nextDouble());
			final double dy = skyBoltDeltaYMin + (skyBoltDeltaYMax - skyBoltDeltaYMin) * rng.nextDouble();
			
			currentX -= dx;
			currentY -= dy;
		} while (currentY >= maxY);
		
		return retval;
	}
	
	/**
	 * Creates a jagged line which
	 * <ul>
	 * <li>starts at the point `origin`
	 * <li>ends at the point `target`
	 * <li>doesn't drift too far from the straight line segment connecting origin and target
	 * <li>
	 * </ul>
	 * 
	 * The generation does involve a {@link java.util.Random}, so this is not a functional function
	 */
	public static List<Point2D> chainPoints(final Point2D origin, final Point2D target) {
		final Random rng = new Random();
		final List<Point2D> retval = new ArrayList<>();
		
		final double distance = origin.distance(target);
		final Point2D parUnitVector = target.subtract(origin).normalize();
		final Point2D perpUnitVector = new Point2D(-parUnitVector.getY(), parUnitVector.getX()).normalize();
		
		retval.add(origin);
		
		double currentParallel = chainPointsParDelta(rng, distance);
		double perpPolarity = (rng.nextBoolean() ? 1.0 : -1.0);
		while (currentParallel < 1.0) {
			final double perp = perpPolarity * (chainPerpMinValue + (chainPerpMaxValue - chainPerpMinValue) * rng.nextDouble());
			
			retval.add(origin.add(parUnitVector.multiply(distance * currentParallel)).add(perpUnitVector.multiply(perp)));
			
			currentParallel += chainPointsParDelta(rng, distance);
			perpPolarity *= -1.0;
		}
		
		retval.add(target);
		
		return retval;
	}
	
	private static double chainPointsParDelta(Random rng, double distance) {
		return (chainDeltaParMin + (chainDeltaParMax - chainDeltaParMin) * rng.nextDouble()) / distance;
	}
	
	/**
	 * Converts a list of points into the format {@link javafx.scene.shape.Polyline} likes
	 */
	private static List<Double> points2coordinates(List<Point2D> ps) {
		return ps.stream().flatMap(p -> Stream.of(p.getX(), p.getY())).collect(Collectors.toList());
	}
	
	private static List<CornerPathSegment> points2cornerPathSegments(List<Point2D> points, double lineWidth) {
		final List<CornerPathSegment> retval = new ArrayList<>(points.size() - 2);
		for (int i = 1; i < points.size() - 1; i++) {
			retval.add(new CornerPathSegment(
				points.get(i),
				points.get(i - 1),
				points.get(i + 1),
				lineWidth
			));
		}
		return retval;
	}
	
	/**
	 * When doing the 'enhanced' electricity fade out, for the purpose of
	 * allowing the jagged lines to recede towards its corners the spark path is
	 * split into sections around each corner, such that one shape consists of the
	 * midpoint of one line segment to the midpoint of the next line segment. This
	 * represents one of those segments.
	 */
	private final static class CornerPathSegment {
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
		
		public CornerPathSegment(
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
	private static final class WritableObservableListWrapper<T> implements WritableValue<List<T>> {
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
	 * An animation which consists of the provided line appearing suddenly,
	 * then fading to transparent gradually
	 */
	public static final class BasicFadeAnimation {
		private static final Duration initalDelayDur = Duration.seconds(0.3);
		private static final Duration explodeDur = Duration.seconds(0.2);
		private static final Duration fadeOutDur = Duration.seconds(0.5);
		
		private static final Duration initalDelayStartTime = Duration.ZERO;
		private static final Duration initalDelayEndTime = initalDelayStartTime.add(initalDelayDur);
		private static final Duration explodeStartTime = initalDelayEndTime;
		private static final Duration explodeEndTime = explodeStartTime.add(explodeDur);
		private static final Duration fadeOutStartTime = explodeEndTime;
		private static final Duration fadeOutEndTime = fadeOutStartTime.add(fadeOutDur);
		
		private final Polyline sharpLine;
		private final Polyline blurredLine;
		private final WritableValue<List<Double>> sharpLinePoints;
		private final WritableValue<List<Double>> blurredLinePoints;
		private final BiFunction<Point2D, Point2D, List<Point2D>> lineGenerator;
		
		public BasicFadeAnimation(
			  final BiFunction<Point2D, Point2D, List<Point2D>> lineGenerator
			, final Group foreground
			, final Group background
		) {
			this.lineGenerator = lineGenerator;
			
			this.sharpLine = new Polyline();
			this.sharpLine.setOpacity(0);
			this.sharpLine.setStroke(Color.hsb(240, 0.2, 0.95, 0.9));
			this.sharpLine.setStrokeWidth(3);
			this.sharpLine.setStrokeLineCap(StrokeLineCap.ROUND);
			this.sharpLine.setEffect(new GaussianBlur(1));
			
			this.blurredLine = new Polyline();
			this.blurredLine.setOpacity(0);
			this.blurredLine.setStroke(Color.hsb(250, 0.1, 0.85, 0.7));
			this.blurredLine.setStrokeWidth(12);
			this.blurredLine.setStrokeLineCap(StrokeLineCap.ROUND);
			this.blurredLine.setEffect(new GaussianBlur(6));
			this.blurredLine.setBlendMode(BlendMode.SCREEN);
			
			this.sharpLinePoints = new WritableObservableListWrapper<>(sharpLine.getPoints());
			this.blurredLinePoints = new WritableObservableListWrapper<>(blurredLine.getPoints());
			
			foreground.getChildren().add(sharpLine);
			foreground.getChildren().add(blurredLine);
		}
		
		public Animation getAnimation(
			Point2D origin,
			Point2D target,
			Animation panAnimation,
			Animation hpAndShakeAnimation
		) {
			final List<Double> coords = points2coordinates(lineGenerator.apply(origin, target));
			
			final Timeline timeline = new Timeline();
			timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
				new KeyValue(sharpLinePoints, coords, Interpolator.DISCRETE),
				new KeyValue(blurredLinePoints, coords, Interpolator.DISCRETE),
				new KeyValue(sharpLine.opacityProperty(), 0.0, Interpolator.DISCRETE),
				new KeyValue(blurredLine.opacityProperty(), 0.0, Interpolator.DISCRETE)
			));
			timeline.getKeyFrames().add(new KeyFrame(explodeStartTime,
				new KeyValue(sharpLine.opacityProperty(), 1.0, Interpolator.DISCRETE),
				new KeyValue(blurredLine.opacityProperty(), 1.0, Interpolator.DISCRETE)
			));
			timeline.getKeyFrames().add(new KeyFrame(fadeOutStartTime,
				new KeyValue(sharpLine.opacityProperty(), 1.0, Interpolator.LINEAR),
				new KeyValue(blurredLine.opacityProperty(), 1.0, Interpolator.LINEAR)
			));
			timeline.getKeyFrames().add(new KeyFrame(fadeOutEndTime,
				new KeyValue(sharpLinePoints, coords, Interpolator.DISCRETE),
				new KeyValue(blurredLinePoints, coords, Interpolator.DISCRETE),
				new KeyValue(sharpLine.opacityProperty(), 0.0, Interpolator.LINEAR),
				new KeyValue(blurredLine.opacityProperty(), 0.0, Interpolator.LINEAR)
			));
			
			return new ParallelTransition(
				panAnimation,
				timeline,
				new SequentialTransition(
					new PauseTransition(explodeStartTime),
					hpAndShakeAnimation
				)
			);
		}
	}
	
	/**
	 * An animation which consists of the provided line appearing suddenly,
	 * flickering between that and a different line, then flickering while getting smaller
	 * 
	 * @see <a href="https://www.patreon.com/posts/electricity-7075305">Medeiros electricity animation tutorial</a>
	 */
	public static final class DisapitateAnimation {
		private static final double sharpLineWidth = 3;
		private static final double blurredLineWidth = 12;
		private static final Color sharpColor = Color.hsb(240, 0.15, 0.95, 0.9);
		private static final Color sharpColorBright = Color.hsb(260, 0.05, 0.95);
		private static final Color blurredColor = Color.hsb(250, 0.1, 0.85, 0.7);
		private static final Color blurredColorBright = Color.hsb(270, 0.05, 0.85, 0.7);
		private static final Duration flickerRate = Duration.seconds(1f / 12f);
		
		private final Path sharpShape;
		private final Path blurredShape;
		private final WritableValue<List<PathElement>> sharpShapePath;
		private final WritableValue<List<PathElement>> blurredShapePath;
		private final BiFunction<Point2D, Point2D, List<Point2D>> lineGenerator;
		
		public DisapitateAnimation(
			  final BiFunction<Point2D, Point2D, List<Point2D>> lineGenerator
			, final Group foreground
			, final Group background
		) {
			this.lineGenerator = lineGenerator;
			
			this.sharpShape = new Path();
			this.sharpShape.setStroke(Color.TRANSPARENT);
			this.sharpShape.setEffect(new GaussianBlur(1));
			
			this.blurredShape = new Path();
			this.blurredShape.setStroke(Color.TRANSPARENT);
			this.blurredShape.setEffect(new GaussianBlur(6));
			this.blurredShape.setBlendMode(BlendMode.SCREEN);
			
			this.sharpShapePath = new WritableObservableListWrapper<>(sharpShape.getElements());
			this.blurredShapePath = new WritableObservableListWrapper<>(blurredShape.getElements());
			
			foreground.getChildren().add(sharpShape);
			foreground.getChildren().add(blurredShape);
		}
		
		public Animation getAnimation(
			Point2D origin,
			Point2D target,
			Animation panAnimation,
			Animation hpAndShakeAnimation
		) {
			final List<Point2D> line1 = lineGenerator.apply(origin, target);
			final List<Point2D> line2 = lineGenerator.apply(origin, target);
			final List<CornerPathSegment> sharpParts1 = points2cornerPathSegments(line1, sharpLineWidth);
			final List<CornerPathSegment> sharpParts2 = points2cornerPathSegments(line2, sharpLineWidth);
			final List<CornerPathSegment> blurredParts1 = points2cornerPathSegments(line1, blurredLineWidth);
			final List<CornerPathSegment> blurredParts2 = points2cornerPathSegments(line2, blurredLineWidth);
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
	}
}
