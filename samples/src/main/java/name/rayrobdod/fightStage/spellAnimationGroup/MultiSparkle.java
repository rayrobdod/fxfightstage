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

import static name.rayrobdod.fightStage.PathElements.newBoundCubicCurveTo;
import static name.rayrobdod.fightStage.PathElements.newBoundMoveTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * This doesn't demonstrate anything, but it is a port of an animation I made in a different system.
 */
public final class MultiSparkle implements SpellAnimationGroup {
	
	private static final Duration sequenceDelay = Duration.millis(200);
	private static final Duration sparkleDuration = Duration.millis(600);
	
	//private static final Color color0 = Color.color(0.6, 0.6, 0.4, 0);
	private static final Color color0 = Color.color(1, 1, 0.6, 0);
	private static final Color color1 = Color.color(0.9, 0.9, 0.3, 0.9);
	private static final Color color2 = color0;
	private static final int halfWidth = 20;
	private static final int halfHeight = 30;
	private static final int distanceFromTarget = 30;
	
	private final Group foreground;
	private final Group background;
	
	private final DoubleProperty targetX;
	private final DoubleProperty targetY;
	private final DoubleProperty orientation;
	private final List<ObjectProperty<Paint>> shapeColors;
	
	public MultiSparkle() {
		this.targetX = new SimpleDoubleProperty();
		this.targetY = new SimpleDoubleProperty();
		this.orientation = new SimpleDoubleProperty();
		
		final Shape[] shapes = {
			newSparkle(
				targetX.add(orientation.multiply(distanceFromTarget * Math.sqrt(3) / 2)),
				targetY.add(distanceFromTarget / 2),
				constantBinding(halfWidth),
				constantBinding(halfHeight)
			),
			newSparkle(
				targetX.add(orientation.multiply(distanceFromTarget / 4)),
				targetY.subtract(distanceFromTarget * Math.sqrt(3) / 2),
				constantBinding(halfWidth),
				constantBinding(halfHeight)
			),
			newSparkle(
				targetX.subtract(orientation.multiply(distanceFromTarget)),
				targetY,
				constantBinding(halfWidth),
				constantBinding(halfHeight)
			)
		};
		
		this.foreground = new Group(shapes);
		this.background = new Group();
		
		Arrays.asList(shapes).forEach(shape -> {
			shape.setStroke(Color.TRANSPARENT);
			shape.setFill(color0);
		});
		
		this.shapeColors = Arrays.stream(shapes).map(x -> x.fillProperty()).collect(Collectors.toList());
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		List<Stream<KeyFrame>> frames = new ArrayList<>();
		
		// Needs Stream::zip(Stream) in order to do this functionally
		for (int i = 0; i < shapeColors.size(); i++) {
			final Duration offset = sequenceDelay.multiply(i);
			final ObjectProperty<Paint> fill = shapeColors.get(i);
			
			frames.add(sparkleKeyFrames(offset, fill));
		}
		
		frames.add(
			Stream.of(
				new KeyFrame(Duration.ZERO,
					new KeyValue(targetX, target.getX(), Interpolator.DISCRETE),
					new KeyValue(targetY, target.getY(), Interpolator.DISCRETE),
					new KeyValue(orientation, Math.signum(origin.getX() - target.getX()), Interpolator.DISCRETE)
				),
				new KeyFrame(Duration.ONE,
					new KeyValue(targetX, target.getX(), Interpolator.DISCRETE),
					new KeyValue(targetY, target.getY(), Interpolator.DISCRETE),
					new KeyValue(orientation, Math.signum(origin.getX() - target.getX()), Interpolator.DISCRETE)
				)
			)
		);
		
		return new SequentialTransition(
			panAnimation.panToDefender(),
			new ParallelTransition(
				new Timeline(
					frames.stream().flatMap(x -> x).toArray(KeyFrame[]::new)
				),
				new SequentialTransition(
					new PauseTransition(sparkleDuration.divide(2).add(sequenceDelay.multiply(0))),
					shakeAnimation.apply()
				),
				new SequentialTransition(
					new PauseTransition(sparkleDuration.divide(2).add(sequenceDelay.multiply(1))),
					new ParallelTransition(
						hitAnimation,
						shakeAnimation.apply()
					)
				),
				new SequentialTransition(
					new PauseTransition(sparkleDuration.divide(2).add(sequenceDelay.multiply(2))),
					shakeAnimation.apply()
				)
			)
		);
	}
	
	private Shape newSparkle(
		DoubleExpression cX, DoubleExpression cY,
		DoubleExpression halfWidth, DoubleExpression halfHeight
	) {
		DoubleExpression maxX = cX.add(halfWidth);
		DoubleExpression maxY = cY.add(halfHeight);
		DoubleExpression minX = cX.subtract(halfWidth);
		DoubleExpression minY = cY.subtract(halfHeight);
		
		return new Path(
			newBoundMoveTo(cX, minY),
			newBoundCubicCurveTo(
				cX, minY.add(cY).divide(2),
				cX.add(maxX).divide(2), cY,
				maxX, cY
			),
			newBoundCubicCurveTo(
				cX.add(maxX).divide(2), cY,
				cX, maxY.add(cY).divide(2),
				cX, maxY
			),
			newBoundCubicCurveTo(
				cX, maxY.add(cY).divide(2),
				cX.add(minX).divide(2), cY,
				minX, cY
			),
			newBoundCubicCurveTo(
				cX.add(minX).divide(2), cY,
				cX, minY.add(cY).divide(2),
				cX, minY
			)
		);
	}
	
	private DoubleBinding constantBinding(final double value) {
		return new DoubleBinding() {
			@Override protected double computeValue() {
				return value;
			}
		};
	}
	
	private Stream<KeyFrame> sparkleKeyFrames(Duration offset, ObjectProperty<Paint> shapeFill) {
		return Stream.of(
			new KeyFrame(offset,
				new KeyValue(shapeFill, color0, Interpolator.LINEAR)
			),
			new KeyFrame(offset.add(sparkleDuration.divide(2)),
				new KeyValue(shapeFill, color1, Interpolator.EASE_OUT)
			),
			new KeyFrame(offset.add(sparkleDuration),
				new KeyValue(shapeFill, color2, Interpolator.EASE_IN)
			)
		);
	}
}
