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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * Demonstrates a camera not ending at the spell target
 */
public final class Nosferatu implements SpellAnimationGroup {
	private static final int spokeCount = 6;
	private static final double maxSpokeRadius = 10;
	private static final double maxSpokeDistance = 60;
	private static final Duration fadeInTime = Duration.seconds(0.5);
	private static final Duration pauseTime = Duration.seconds(0.1);
	private static final Duration fadeOutTime = Duration.seconds(1.0);
	private static final Color mainColor = Color.rgb(224, 196, 255);
	
	private static final double spokeAngle = 2 * Math.PI / spokeCount;
	private static final double initSpokeAngle = spokeAngle / 2;
	
	private final Group frontLayer;
	private final Group backLayer;
	private final Group background;
	private final List<Circle> spokes;
	
	public Nosferatu() {
		this.frontLayer = new Group();
		this.backLayer = new Group();
		this.background = new Group();
		this.spokes = createCircles(spokeCount, frontLayer);
	}
	
	private static List<Circle> createCircles(int count, Group addTo) {
		return Stream.generate(Circle::new)
				.limit(count)
				.peek(x -> {
					x.setFill(Color.BLACK);
					x.setBlendMode(BlendMode.EXCLUSION);
					addTo.getChildren().add(x);
				})
				.collect(Collectors.toList());
	}
	
	public Node backgroundLayer() { return this.background; }
	public Node objectBehindLayer() { return this.backLayer; }
	public Node objectFrontLayer() { return this.frontLayer; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Animation fadeIn = new Timeline(
			targetKeyFrame(Duration.ZERO, target, Color.BLACK, 0, 0),
			targetKeyFrame(fadeInTime, target, mainColor, maxSpokeRadius, maxSpokeDistance)
		);
		final Animation pause = new PauseTransition(pauseTime);
		final Animation fadeOut = fadeOutAnim(target, origin);
		
		return new SequentialTransition(
			panAnimation.panToDefender(),
			new ParallelTransition(
				fadeIn,
				shakeAnimation.apply(),
				hitAnimation
			),
			pause,
			new ParallelTransition(
				panAnimation.panToAttacker(fadeOutTime),
				fadeOut
			)
		);
	}
	
	private KeyFrame targetKeyFrame(
		final Duration time,
		final Point2D target,
		final Color color,
		final double spokeRadius,
		final double spokeDistance
	) {
		final List<KeyValue> retval = new ArrayList<>();
		
		for (int i = 0; i < spokes.size(); i++) {
			Circle spoke = spokes.get(i);
			Point2D p = targetSidePoint(i, target, spokeDistance);
			
			retval.addAll(java.util.Arrays.asList(
				// PathTransition uses the `translate#` properties, so this too must
				// use the `translate#` properties instead of the `center#` properties
				new KeyValue(spoke.translateXProperty(), p.getX(), Interpolator.LINEAR),
				new KeyValue(spoke.translateYProperty(), p.getY(), Interpolator.LINEAR),
				new KeyValue(spoke.radiusProperty(), spokeRadius, Interpolator.LINEAR),
				new KeyValue(spoke.fillProperty(), color, Interpolator.LINEAR)
			));
		}
		
		return new KeyFrame(time, retval.stream().toArray(KeyValue[]::new));
	}
	
	private static Point2D targetSidePoint(
		final int index,
		final Point2D target,
		final double spokeDistance
	) {
		final double angle = initSpokeAngle + index * spokeAngle;
		final double dx = Math.cos(angle) * spokeDistance;
		final double dy = Math.sin(angle) * spokeDistance;
		return new Point2D(target.getX() + dx, target.getY() + dy);
	}
	
	private Animation fadeOutAnim(final Point2D target, final Point2D origin) {
		final List<Animation> retval = new ArrayList<>();
		
		for (int i = 0; i < spokes.size(); i++) {
			final Circle spoke = spokes.get(i);
			final Point2D startPoint = targetSidePoint(i, target, maxSpokeDistance);
			final Point2D control1Point = targetSidePoint(i, target, maxSpokeDistance * 2);
			final Point2D control2Point = new Point2D((control1Point.getX() + origin.getX()) / 2, control1Point.getY());
			// final Point2D endPoint = new Point2D(origin.getX() + 50 * Math.signum(origin.getX() - target.getX()), origin.getY());
			final Point2D endPoint = origin;
			
			CubicCurve shape = new CubicCurve(
				startPoint.getX(), startPoint.getY(),
				control1Point.getX(), control1Point.getY(),
				control2Point.getX(), control2Point.getY(),
				endPoint.getX(), endPoint.getY()
			);
			
			retval.add(new PathTransition(fadeOutTime, shape, spoke));
		}
		
		retval.add(new Timeline(
			fadeOutAnimKeyFrame(Duration.ZERO, mainColor),
			fadeOutAnimKeyFrame(fadeOutTime.multiply(0.4), mainColor),
			fadeOutAnimKeyFrame(fadeOutTime.multiply(0.9), Color.BLACK)
		));
		
		return new ParallelTransition(retval.stream().toArray(Animation[]::new));
	}
	
	private KeyFrame fadeOutAnimKeyFrame(Duration time, Color color) {
		final List<KeyValue> retval = new ArrayList<>();
		for (int i = 0; i < spokes.size(); i++) {
			Circle spoke = spokes.get(i);
			retval.add(new KeyValue(spoke.fillProperty(), color, Interpolator.LINEAR));
		}
		return new KeyFrame(time, retval.stream().toArray(KeyValue[]::new));
	}
}
