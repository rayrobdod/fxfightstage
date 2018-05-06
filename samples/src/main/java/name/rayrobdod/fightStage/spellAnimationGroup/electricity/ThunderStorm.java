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
package name.rayrobdod.fightStage.spellAnimationGroup.electricty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
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
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * Combines a set of ElectricAnimations with a few other things to create a cohesive SpellAnimation
 */
public final class ThunderStorm implements SpellAnimationGroup {
	private static final int cloudSparkCount = 6;
	private static final int enemySparkCount = 4;
	
	private final Group background;
	private final Group foreground;
	
	private final DoubleProperty cloudOpacity;
	private final DoubleProperty cloudLeftTranslateX;
	private final DoubleProperty cloudRightTranslateX;
	private final List<ElectricAnimationFactory> cloudSparks;
	private final List<ElectricAnimationFactory> enemySparks;
	
	public ThunderStorm() {
		final Translate cloudLeftTranslate = new Translate();
		final Translate cloudRightTranslate = new Translate();
		
		final Group clouds = ThunderStorm.createClouds(cloudLeftTranslate, cloudRightTranslate);
		this.cloudOpacity = clouds.opacityProperty();
		this.cloudLeftTranslateX = cloudLeftTranslate.xProperty();
		this.cloudRightTranslateX = cloudRightTranslate.xProperty();
		this.cloudOpacity.set(0);
		
		this.background = new Group(clouds);
		this.foreground = new Group();
		
		final ChainPoints chainPoints = new ChainPoints();
		final SkyBoltPoints skyBoltPoints = new SkyBoltPoints();
		this.cloudSparks = Stream
				.generate(() -> new DissipateElectricAnimationFactory(chainPoints, background))
				.limit(cloudSparkCount)
				.collect(Collectors.toList());
		this.enemySparks = Stream
				.generate(() -> Stream.of(
					new FadeElectricAnimationFactory(skyBoltPoints, foreground),
					new DissipateElectricAnimationFactory(skyBoltPoints, foreground)
				))
				.flatMap(Function.identity())
				.limit(enemySparkCount)
				.collect(Collectors.toList());
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
		final Random rng = new Random();
		final Point2D midpoint = origin.midpoint(target);
		
		Timeline cloudInAnimation = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(cloudOpacity, 0.0, Interpolator.DISCRETE),
				new KeyValue(cloudLeftTranslateX, midpoint.getX() - 500, Interpolator.DISCRETE),
				new KeyValue(cloudRightTranslateX, midpoint.getX() + 500, Interpolator.DISCRETE)
			),
			new KeyFrame(Duration.millis(1500),
				new KeyValue(cloudOpacity, 1.0, Interpolator.EASE_IN),
				new KeyValue(cloudLeftTranslateX, midpoint.getX(), Interpolator.EASE_OUT),
				new KeyValue(cloudRightTranslateX, midpoint.getX(), Interpolator.EASE_OUT)
			)
		);
		Timeline cloudOutAnimation = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(cloudOpacity, 1.0, Interpolator.DISCRETE)
			),
			new KeyFrame(Duration.millis(750),
				new KeyValue(cloudOpacity, 0.0, Interpolator.EASE_OUT)
			)
		);
		
		Animation cloudSparkAnim = new ParallelTransition(
			cloudSparks.stream()
				.map(eaf -> eaf.getAnimation(
					createCloudSparkPoint(rng, origin, midpoint),
					createCloudSparkPoint(rng, target, midpoint),
					Animations.nil(), ShakeAnimationBiFunction.nil(), Animations.nil()
				))
				.map(anim -> new SequentialTransition(
					new PauseTransition(Duration.seconds(rng.nextDouble() * 2)),
					anim
				))
				.toArray(Animation[]::new)
		);
		// Separate the first enemy spark from the others because
		// `hpAndShakeAnimation` has a "called once and only once" requirement,
		// and this allows me to put said animation in one place
		Animation firstEnemySparkAnim = enemySparks.get(0).getAnimation(
				origin, target, Animations.nil(), shakeAnimation, hitAnimation);
		Animation enemySparkAnim = new ParallelTransition(
			enemySparks.stream()
				.skip(1)
				.map(eaf -> eaf.getAnimation(
					origin, target,
					Animations.nil(), ShakeAnimationBiFunction.nil(), Animations.nil()
				))
				.toArray(Animation[]::new)
		);
		
		return new SequentialTransition(
			new ParallelTransition(
				cloudInAnimation,
				new SequentialTransition(
					new PauseTransition(cloudInAnimation.getCycleDuration().divide(2)),
					cloudSparkAnim
				),
				new SequentialTransition(
					new PauseTransition(cloudInAnimation.getCycleDuration().divide(2).add(cloudSparkAnim.getCycleDuration().divide(2))),
					panAnimation
				)
			),
			new PauseTransition(Duration.millis(150)),
			new ParallelTransition(
				firstEnemySparkAnim,
				enemySparkAnim
			),
			cloudOutAnimation
		);
	}
	
	private static Point2D createCloudSparkPoint(Random rng, Point2D one, Point2D two) {
		final Point2D midpoint = one.midpoint(two);
		final double dx = Math.max(300, (one.getX() - two.getX()) * 1.4);
		
		return new Point2D(
			midpoint.getX() + dx * (rng.nextDouble() - 0.5),
			midpoint.getY() - 150 + 50 * rng.nextDouble()
		);
	}
	
	private static Group createClouds(Translate translateLeft, Translate translateRight) {
		final Random rng = new Random();
		final int xRange = 500;
		
		final List<Circle> circles = new ArrayList<>();
		for (int y = -150; y > -600; y -= 25) {
			final double yPercent = (y + 600.0) / 500.0;
			final Color fill = Color.hsb(0, 0, (1 - yPercent) * 0.5 + 0.3);
			
			for (int x = 0; x < xRange; x += 25) {
				final double leftX = x - xRange + 100 * yPercent;
				final double rightX = x - 100 * yPercent;
				
				// slightly vary the circles so they don't look unnaturally uniform
				final double radius = 30 + 20 * rng.nextDouble();
				final double dx = 10 * rng.nextDouble() - 5;
				final double dy = 10 * rng.nextDouble() - 5;
				
				Circle leftCircle = new Circle(leftX + dx, y + dy, radius, fill);
				leftCircle.getTransforms().add(translateLeft);
				circles.add(leftCircle);
				
				Circle rightCircle = new Circle(rightX + dx, y + dy, radius, fill);
				rightCircle.getTransforms().add(translateRight);
				circles.add(rightCircle);
			}
		}
		
		return new Group(circles.toArray(new Circle[0]));
	}
}
