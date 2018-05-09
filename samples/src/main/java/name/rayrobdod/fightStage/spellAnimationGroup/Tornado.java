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

import static name.rayrobdod.fightStage.BattleAnimation.GROUND_Y;
import static name.rayrobdod.fightStage.PathElements.newBoundCubicCurveTo;
import static name.rayrobdod.fightStage.PathElements.newBoundMoveTo;

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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * Particle Effects v2
 */
public final class Tornado implements SpellAnimationGroup {
	
	private static final Color color = Color.color(0.2, 0.9, 0.2, 0.9);
	
	private static final double tornadoHeight = 200;
	private static final double tornadoTopWidth = 125;
	private static final double tornadoBotWidth = 75;
	
	private static final Duration particleMaxDuration = Duration.seconds(0.75);
	private static final Duration timeBetweenParticles = Duration.millis(50);
	private static final Duration particleGenerationTime = Duration.seconds(1.0);
	private static final int particleCount = (int) Math.ceil(particleGenerationTime.toMillis() / timeBetweenParticles.toMillis());
	
	private static final double perspectiveMultiplier = -0.4;
	private static final double crescentWidthMultiplier = 0.8;
	
	private final Group foreground;
	private final Group background;
	private final CrescentPathParts[] particlePool;
	
	private static final class CrescentPathParts {
		public final DoubleProperty leftXProperty;
		public final DoubleProperty leftYProperty;
		public final DoubleProperty rightXProperty;
		public final DoubleProperty rightYProperty;
		public final Path path;
		
		public CrescentPathParts() {
			this.leftXProperty = new SimpleDoubleProperty();
			this.leftYProperty = new SimpleDoubleProperty();
			this.rightXProperty = new SimpleDoubleProperty();
			this.rightYProperty = new SimpleDoubleProperty();
			
			final DoubleBinding perspectiveY = new DoubleBinding() {
				{
					super.bind(leftXProperty);
					super.bind(rightXProperty);
				}
				
				@Override
				protected double computeValue() {
					return Math.abs(leftXProperty.getValue() - rightXProperty.getValue()) * perspectiveMultiplier;
				}
			};
			
			this.path = new Path(
				newBoundMoveTo(
					leftXProperty, leftYProperty
				),
				newBoundCubicCurveTo(
					leftXProperty,
					leftYProperty.subtract(perspectiveY),
					rightXProperty,
					rightYProperty.subtract(perspectiveY),
					rightXProperty,
					rightYProperty
				),
				newBoundCubicCurveTo(
					rightXProperty,
					rightYProperty.subtract(perspectiveY.multiply(crescentWidthMultiplier)),
					leftXProperty,
					leftYProperty.subtract(perspectiveY.multiply(crescentWidthMultiplier)),
					leftXProperty,
					leftYProperty
				)
			);
			this.path.setFill(color);
			this.path.setStroke(Color.TRANSPARENT);
			this.path.setOpacity(0.0);
		}
	}
	
	public Tornado() {
		this.background = new Group();
		this.foreground = new Group();
		
		this.particlePool = createParticlePool(particleCount, this.foreground);
	}
	
	private static CrescentPathParts[] createParticlePool(int count, Group addTo) {
		return Stream.generate(CrescentPathParts::new)
				.limit(count)
				.peek(x -> addTo.getChildren().add(x.path))
				.toArray(CrescentPathParts[]::new);
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final PerlinNoise noise = new PerlinNoise(new Random());
		final Timeline effectTimeline = new Timeline();
		
		Duration iTime = Duration.ZERO;
		for (int i = 0; i < particleCount; i++) {
			
			effectTimeline.getKeyFrames().addAll(
				particleKeyFrames(
					iTime,
					i,
					noise,
					target.getX()
				)
			);
			iTime = iTime.add(timeBetweenParticles);
		}
		
		return new ParallelTransition(
			panAnimation,
			effectTimeline,
			new SequentialTransition(
				new PauseTransition(particleGenerationTime.divide(4)),
				new ParallelTransition(
					shakeAnimation.apply(4, particleGenerationTime),
					hitAnimation
				)
			)
		);
	}
	
	private final List<KeyFrame> particleKeyFrames(
		final Duration startTime,
		final int particlePoolIndex,
		final PerlinNoise noise,
		final double centerX
	) {
		final List<KeyFrame> retval = new java.util.LinkedList<>();
		final CrescentPathParts particle = particlePool[particlePoolIndex];
		
		retval.addAll(java.util.Arrays.asList(
			new KeyFrame(startTime,
					new KeyValue(particle.path.opacityProperty(), 0.0, Interpolator.DISCRETE)),
			new KeyFrame(startTime.add(particleMaxDuration.multiply(1d / 6d)),
					new KeyValue(particle.path.opacityProperty(), 1.0, Interpolator.EASE_OUT)),
			new KeyFrame(startTime.add(particleMaxDuration.multiply(2d / 3d)),
					new KeyValue(particle.path.opacityProperty(), 1.0, Interpolator.LINEAR)),
			new KeyFrame(startTime.add(particleMaxDuration),
					new KeyValue(particle.path.opacityProperty(), 0.0, Interpolator.EASE_IN))
		));
		
		/* No perlin noise version
		
		retval.add(new KeyFrame(startTime,
			new KeyValue(particle.leftYProperty, GROUND_Y, Interpolator.DISCRETE),
			new KeyValue(particle.rightYProperty, GROUND_Y, Interpolator.DISCRETE),
			new KeyValue(particle.leftXProperty, centerX - tornadoBotWidth / 2, Interpolator.DISCRETE),
			new KeyValue(particle.rightXProperty, centerX + tornadoBotWidth / 2, Interpolator.DISCRETE)
		));
		retval.add(new KeyFrame(startTime.add(particleMaxDuration),
			new KeyValue(particle.leftYProperty, GROUND_Y - tornadoHeight, Interpolator.LINEAR),
			new KeyValue(particle.rightYProperty, GROUND_Y - tornadoHeight, Interpolator.LINEAR),
			new KeyValue(particle.leftXProperty, centerX - tornadoTopWidth / 2, Interpolator.EASE_OUT),
			new KeyValue(particle.rightXProperty, centerX + tornadoTopWidth / 2, Interpolator.EASE_OUT)
		));
		*/
		
		final double startTimeSecs = startTime.toSeconds();
		for (double i = 0; i < particleMaxDuration.toSeconds(); i += 1d / 60d) {
			final double currentTimeSecs = startTimeSecs + i;
			final Duration currentTime = Duration.seconds(startTimeSecs + i);
			final double fraction = i / particleMaxDuration.toSeconds();
			
			final double widthNoDrift = Interpolator.EASE_OUT.interpolate(tornadoBotWidth, tornadoTopWidth, fraction);
			final double yNoDrift = Interpolator.LINEAR.interpolate(GROUND_Y, GROUND_Y - tornadoHeight, fraction);
			
			final double leftY = yNoDrift + noise.sum2D(particlePoolIndex / 10d, i * 12d, 4, 2, 2) * 20;
			final double rightY = yNoDrift + noise.sum2D(particlePoolIndex / 10d, 64 + i * 12d, 4, 2, 2) * 20;
			final double leftX = centerX - widthNoDrift / 2 + noise.sum2D(particlePoolIndex / 10d, 128 + i * 12d, 4, 2, 2) * 40;
			final double rightX = centerX + widthNoDrift / 2 + noise.sum2D(particlePoolIndex / 10d, 192 + i * 12d, 4, 2, 2) * 40;
			
			retval.add(new KeyFrame(currentTime,
				new KeyValue(particle.leftYProperty, leftY, Interpolator.LINEAR),
				new KeyValue(particle.rightYProperty, rightY, Interpolator.LINEAR),
				new KeyValue(particle.leftXProperty, leftX, Interpolator.LINEAR),
				new KeyValue(particle.rightXProperty, rightX, Interpolator.LINEAR)
			));
		}
		
		return retval;
	}
}
