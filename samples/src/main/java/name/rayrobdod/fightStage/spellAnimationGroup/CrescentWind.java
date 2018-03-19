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

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * For the sake of showing a shape that isn't vertically symmetrical
 */
public final class CrescentWind implements SpellAnimationGroup {
	
	private static final Duration windupDuration = Duration.seconds(0.2);
	private static final double windupDistance = 100;
	private static final double fadeoutDistance = 200;
	private static final double speed = 400;
	private static final Color color = Color.color(0.2, 0.9, 0.2, 0.6);
	
	private static final double maxHeight = 150;
	private static final double maxWidth = 75;
	
	private final Path foreground;
	private final Path background;
	
	private final DoubleProperty xProperty;
	private final DoubleProperty yProperty;
	private final DoubleProperty heightProperty;
	private final DoubleProperty widthProperty;
	private final DoubleProperty orientationXProperty;
	
	public CrescentWind() {
		this.xProperty = new SimpleDoubleProperty();
		this.yProperty = new SimpleDoubleProperty();
		this.heightProperty = new SimpleDoubleProperty();
		this.widthProperty = new SimpleDoubleProperty();
		this.orientationXProperty = new SimpleDoubleProperty();
		
		///   1/4
		///  / |
		/// 2 |
		///  \ |
		///   3
		final MoveTo foreground_1 = new MoveTo();
		foreground_1.xProperty().bind(this.xProperty.add(this.orientationXProperty.multiply(this.widthProperty)));
		foreground_1.yProperty().bind(this.yProperty.add(this.heightProperty.divide(2)));
		final QuadCurveTo foreground_2 = new QuadCurveTo();
		foreground_2.controlXProperty().bind(this.xProperty);
		foreground_2.controlYProperty().bind(this.yProperty.add(this.heightProperty.divide(3)));
		foreground_2.xProperty().bind(this.xProperty);
		foreground_2.yProperty().bind(this.yProperty);
		final QuadCurveTo foreground_3 = new QuadCurveTo();
		foreground_3.controlXProperty().bind(this.xProperty);
		foreground_3.controlYProperty().bind(this.yProperty.subtract(this.heightProperty.divide(3)));
		foreground_3.xProperty().bind(this.xProperty.add(this.orientationXProperty.multiply(this.widthProperty)));
		foreground_3.yProperty().bind(this.yProperty.subtract(this.heightProperty.divide(2)));
		final CubicCurveTo foreground_4 = new CubicCurveTo();
		foreground_4.controlX1Property().bind(this.xProperty.add(this.orientationXProperty.multiply(this.widthProperty.divide(3))));
		foreground_4.controlY1Property().bind(this.yProperty.subtract(this.heightProperty.divide(3)));
		foreground_4.controlX2Property().bind(this.xProperty.add(this.orientationXProperty.multiply(this.widthProperty.divide(3))));
		foreground_4.controlY2Property().bind(this.yProperty.add(this.heightProperty.divide(3)));
		foreground_4.xProperty().bind(foreground_1.xProperty());
		foreground_4.yProperty().bind(foreground_1.yProperty());
		
		this.foreground = new Path(foreground_1, foreground_2, foreground_3, foreground_4);
		this.foreground.setFill(color);
		this.foreground.setStroke(Color.TRANSPARENT);
		// this.foreground.setBlendMode(javafx.scene.effect.BlendMode.SCREEN);
		this.background = new Path(foreground_1, foreground_2, foreground_3, foreground_4);
		this.background.setFill(color);
		this.background.setStroke(Color.TRANSPARENT);
		this.background.opacityProperty().bind(this.foreground.opacityProperty());
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final double totalDistance = windupDistance + origin.distance(target) + fadeoutDistance;
		final double panDistance = windupDistance + origin.distance(target) / 2;
		final double hitDistance = windupDistance + origin.distance(target);
		
		final Interpolator travelInterpolator = Interpolator.SPLINE(0.6, 0.0, 0.8, 0.8);
		
		final Duration totalTravelDuration = Duration.seconds(totalDistance / speed);
		final Duration panDuration = totalTravelDuration.multiply(reverseInterpolate(travelInterpolator, panDistance / totalDistance));
		final Duration hitDuration = totalTravelDuration.multiply(reverseInterpolate(travelInterpolator, hitDistance / totalDistance));
		
		final double orientation = (origin.getX() < target.getX() ? -1 : 1);
		
		
		final Timeline spellAnimation = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(this.xProperty, origin.getX() + orientation * windupDistance, Interpolator.LINEAR),
				new KeyValue(this.yProperty, target.getY(), Interpolator.LINEAR),
				new KeyValue(this.widthProperty, maxWidth, Interpolator.LINEAR),
				new KeyValue(this.heightProperty, 0.0, Interpolator.LINEAR),
				new KeyValue(this.orientationXProperty, orientation, Interpolator.LINEAR),
				new KeyValue(this.foreground.opacityProperty(), 0.0, Interpolator.LINEAR)
			),
			new KeyFrame(Duration.ONE,
				new KeyValue(this.orientationXProperty, orientation, Interpolator.LINEAR),
				new KeyValue(this.yProperty, target.getY(), Interpolator.LINEAR),
				new KeyValue(this.widthProperty, maxWidth, Interpolator.EASE_OUT)
			),
			new KeyFrame(windupDuration,
				new KeyValue(this.xProperty, origin.getX() + orientation * windupDistance, Interpolator.LINEAR),
				new KeyValue(this.heightProperty, maxHeight, Interpolator.EASE_OUT),
				new KeyValue(this.foreground.opacityProperty(), 1.0, Interpolator.EASE_OUT)
			),
			new KeyFrame(windupDuration.add(hitDuration),
				new KeyValue(this.foreground.opacityProperty(), 1.0, Interpolator.LINEAR)
			),
			new KeyFrame(windupDuration.add(totalTravelDuration),
				new KeyValue(this.xProperty, target.getX() - orientation * fadeoutDistance, travelInterpolator),
				new KeyValue(this.foreground.opacityProperty(), 0.0, Interpolator.EASE_IN)
			)
		);
		
		return new ParallelTransition(
			spellAnimation,
			new SequentialTransition(
				new PauseTransition(windupDuration.add(panDuration)),
				panAnimation
			),
			new SequentialTransition(
				new PauseTransition(windupDuration.add(hitDuration)),
				hpAndShakeAnimation
			)
		);
	}
	
	/**
	 * Returns the value `a` such that `i.interpolate(0.0, 1.0, a)` is within a certain tolerance of `targetValue`
	 * @pre 0.0 <= targetValue && targetValue <= 1.0
	 * @pre the interpolator is strictly increasing / monotone
	 */
//	private double reverseInterpolate(final Interpolator i, final double startValue, final double endValue, final double targetValue) {
	private double reverseInterpolate(final Interpolator i, final double targetValue) {
		final double startValue = 0;
		final double endValue = 1;
		final double precision = Math.abs(startValue - endValue) * 1e-9;
		
		// use a bisection to find a relevant input
		double low = 0.0;
		double high = 1.0;
		while (low != high) {
			final double mid = (high + low) / 2;
			final double result = i.interpolate(startValue, endValue, mid);
			
			if (targetValue - precision < result && result < targetValue + precision) {
				return mid;
			} else if (targetValue < result) {
				high = mid;
			} else {
				low = mid;
			}
		}
		
		return low;
	}
}
