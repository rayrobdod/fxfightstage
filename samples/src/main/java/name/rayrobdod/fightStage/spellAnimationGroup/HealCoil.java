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
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * An animation which places "orbs" in a "cylindrical" pattern around the target
 * <p>
 * Demonstrates a more subtle use of the background buffer
 */
public final class HealCoil implements SpellAnimationGroup {
	
	private static final Duration timePerTick = Duration.seconds(1d / 30d);
	private static final Duration particleVisibleTime = Duration.seconds(1d / 2);
	
	private static final double coilRadius = 60;
	private static final int coilRevolutions = 5;
	private static final double coilHeight = 120;
	private static final double coilHeightPerRevolutions = coilHeight / coilRevolutions;
	private static final double perspectiveMultiplier = 0.2;
	private static final int particlesPerRevolution = 12;
	private static final double coilHeightPerParticle = coilHeightPerRevolutions / particlesPerRevolution;
	
	private static final Color particleColor = Color.BEIGE;
	private static final double particleRadius = 6;
	private static final int particleCount = particlesPerRevolution * (coilRevolutions + 1);
	private static final Effect paritcleEffect = new GaussianBlur(particleRadius);
	
	private final Group foreground;
	private final Group background;
	private final Circle[] particles;
	
	public HealCoil() {
		this.background = new Group();
		this.foreground = new Group();
		
		this.particles = Stream.generate(Circle::new)
				.limit(particleCount)
				.toArray(Circle[]::new);
		
		for (int i = 0; i < particles.length; i++) {
			final Circle p = particles[i];
			final int segment = i % particlesPerRevolution;
			
			p.setFill(particleColor);
			p.setRadius(particleRadius);
			p.setVisible(false);
			p.setEffect(paritcleEffect);
			if (segment < particlesPerRevolution / 2) {
				foreground.getChildren().add(p);
			} else {
				background.getChildren().add(p);
			}
		}
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final Timeline effectTimeline = new Timeline();
		
		for (int i = 0; i < particles.length; i++) {
			final Circle p = particles[i];
			final Duration time = timePerTick.multiply(i);
			final int segment = i % particlesPerRevolution;
			final double heightDueToRevolution = coilHeightPerParticle * Math.max(0, ((double) i) - particlesPerRevolution);
			
			final double x = target.getX() + coilRadius * Math.cos(2 * Math.PI * segment / particlesPerRevolution);
			final double y = target.getY() + (coilHeight * 1/3) - heightDueToRevolution + coilRadius * perspectiveMultiplier * Math.sin(2 * Math.PI * segment / particlesPerRevolution);
			
			effectTimeline.getKeyFrames().addAll(particleKeyFrames(p, time, x, y));
		}
		
		return new SequentialTransition(
			panAnimation,
			effectTimeline,
			hpAndShakeAnimation
		);
	}
	
	
	private static List<KeyFrame> particleKeyFrames(
		Circle particle, Duration startTime, double x, double y
	) {
		return java.util.Arrays.asList(
			new KeyFrame(startTime,
				new KeyValue(particle.centerXProperty(), x, Interpolator.DISCRETE),
				new KeyValue(particle.centerYProperty(), y, Interpolator.DISCRETE),
				new KeyValue(particle.visibleProperty(), false, Interpolator.DISCRETE)
			),
			new KeyFrame(startTime.add(Duration.ONE),
				new KeyValue(particle.visibleProperty(), true, Interpolator.DISCRETE)
			),
			new KeyFrame(startTime.add(particleVisibleTime),
				new KeyValue(particle.visibleProperty(), true, Interpolator.DISCRETE)
			),
			new KeyFrame(startTime.add(particleVisibleTime).add(Duration.ONE),
				new KeyValue(particle.centerXProperty(), x, Interpolator.LINEAR),
				new KeyValue(particle.centerYProperty(), y, Interpolator.LINEAR),
				new KeyValue(particle.visibleProperty(), false, Interpolator.DISCRETE)
			)
		);
	}
}
