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

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * Doesn't really demonstrate anything, but: particle effects yay?
 */
public final class Fireball implements SpellAnimationGroup {
	
	private static final Duration approachTickTime = Duration.seconds(1d / 30d);
	private static final Duration approachTickParticleMaxDuration = Duration.seconds(1d / 2d);
	
	private static final double speed = 7.5;
	private static final int approachTickParticleCount = 3;
	private static final int approachParticlePoolSize = approachTickParticleCount * (int)(
		Math.ceil(approachTickParticleMaxDuration.toMillis() / approachTickTime.toMillis())
	);
	
	private final Group node;
	private final Group background;
	private final Circle[] approachWhiteParticlePool;
	private final Circle[] approachYellowParticlePool;
	private final Circle[] approachBlackParticlePool;
	
	public Fireball() {
		this.background = new Group();
		this.node = new Group();
		
		this.approachBlackParticlePool = createCircles(approachParticlePoolSize, this.node);
		this.approachYellowParticlePool = createCircles(approachParticlePoolSize, this.node);
		this.approachWhiteParticlePool = createCircles(approachParticlePoolSize, this.node);
	}
	
	private static Circle[] createCircles(int count, Group addTo) {
		return Stream.generate(Circle::new)
				.limit(count)
				.peek(x -> addTo.getChildren().add(x))
				.toArray(Circle[]::new);
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.node; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Point2D approachVector = target.subtract(origin);
		final Point2D approachPerTickVector = approachVector.normalize().multiply(speed);
		final Timeline effectTimeline = new Timeline();
		
		Point2D currentApproach = Point2D.ZERO;
		Duration currentTime = Duration.ZERO;
		int approachParticlePoolIndex = 0;
		while (currentApproach.magnitude() < approachVector.magnitude()) {
			effectTimeline.getKeyFrames().addAll(
				approachTickKeyFrames(
					currentTime,
					approachParticlePoolIndex,
					origin.add(currentApproach)
				)
			);
			
			approachParticlePoolIndex += approachTickParticleCount;
			approachParticlePoolIndex %= approachParticlePoolSize;
			currentApproach = currentApproach.add(approachPerTickVector);
			currentTime = currentTime.add(approachTickTime);
		}
		final Duration shakeTime = currentTime;
		
		return new ParallelTransition(
			effectTimeline,
			new SequentialTransition(
				new PauseTransition(shakeTime.divide(2)),
				panAnimation
			),
			new SequentialTransition(
				new PauseTransition(shakeTime),
				new ParallelTransition(
					shakeAnimation.apply(),
					hitAnimation
				)
			)
		);
	}
	
	private final List<KeyFrame> approachTickKeyFrames(
		Duration startTime,
		int approachParticlePoolIndex,
		Point2D centerPoint
	) {
		final Random rng = new Random();
		final List<KeyFrame> retval = new java.util.LinkedList<>();
		
		for (int i = 0; i < approachTickParticleCount; i++) {
			final Point2D deltaVec = new Point2D(
				rng.nextDouble() * speed * Math.cos(Math.PI * 2 * i / approachTickParticleCount),
				rng.nextDouble() * speed * Math.sin(Math.PI * 2 * i / approachTickParticleCount)
			);
			
			retval.addAll(approachParticleKeyFrames(
				approachWhiteParticlePool[approachParticlePoolIndex + i],
				startTime, startTime.add(approachTickParticleMaxDuration.multiply(1d / 3d)),
				centerPoint, centerPoint.add(deltaVec),
				Color.rgb(255, 240, 180, 0.7), Color.rgb(255, 240, 180, 0.5), Color.rgb(255, 240, 180, 0),
				speed * 2 / 3, speed * 2 / 3, speed / 3
			));
		}
		
		for (int i = 0; i < approachTickParticleCount; i++) {
			final Point2D deltaVec = new Point2D(
				rng.nextDouble() * 1.75 * speed * Math.cos(Math.PI * 2 * i / approachTickParticleCount),
				rng.nextDouble() * 1.75 * speed * Math.sin(Math.PI * 2 * i / approachTickParticleCount)
			);
			
			retval.addAll(approachParticleKeyFrames(
				approachYellowParticlePool[approachParticlePoolIndex + i],
				startTime, startTime.add(approachTickParticleMaxDuration.multiply(0.5)),
				centerPoint, centerPoint.add(deltaVec),
				Color.rgb(220,220,20), Color.rgb(220, 120, 0, 0.8), Color.rgb(200, 40, 0, 0),
				1.5 * speed + 2, 1.5 * speed + 2, 0.75 * speed + 2
			));
		}
		
		for (int i = 0; i < approachTickParticleCount; i++) {
			final Point2D deltaVec = new Point2D(
				rng.nextDouble() * speed * Math.cos(Math.PI * 2 * i / approachTickParticleCount),
				rng.nextDouble() * speed * Math.sin(Math.PI * 2 * i / approachTickParticleCount)
			);
			
			retval.addAll(approachParticleKeyFrames(
				approachBlackParticlePool[approachParticlePoolIndex + i],
				startTime, startTime.add(approachTickParticleMaxDuration.multiply(0.888)),
				centerPoint, centerPoint.add(deltaVec),
				Color.rgb(55,55,55,0.8), Color.rgb(55,55,55, 0.7), Color.rgb(55,55,55, 0),
				speed + 2, speed + 2, speed + 2
			));
		}
		
		return retval;
	}
	
	private static List<KeyFrame> approachParticleKeyFrames(
		Circle particle,
		Duration startTime, Duration endTime,
		Point2D startPoint, Point2D endPoint,
		Color startColor, Color midColor, Color endColor,
		double startRadius, double midRadius, double endRadius
	) {
		final Duration midTime = startTime.add(endTime.subtract(startTime).divide(2));
		
		return java.util.Arrays.asList(
			new KeyFrame(startTime,
				new KeyValue(particle.centerXProperty(), startPoint.getX(), Interpolator.DISCRETE),
				new KeyValue(particle.centerYProperty(), startPoint.getY(), Interpolator.DISCRETE),
				new KeyValue(particle.radiusProperty(), startRadius, Interpolator.DISCRETE),
				new KeyValue(particle.fillProperty(), startColor, Interpolator.DISCRETE)
			),
			new KeyFrame(midTime,
				new KeyValue(particle.radiusProperty(), midRadius, Interpolator.LINEAR),
				new KeyValue(particle.fillProperty(), midColor, Interpolator.LINEAR)
			),
			new KeyFrame(endTime,
				new KeyValue(particle.centerXProperty(), endPoint.getX(), Interpolator.LINEAR),
				new KeyValue(particle.centerYProperty(), endPoint.getY(), Interpolator.LINEAR),
				new KeyValue(particle.radiusProperty(), endRadius, Interpolator.LINEAR),
				new KeyValue(particle.fillProperty(), endColor, Interpolator.LINEAR)
			)
		);
	}
}
