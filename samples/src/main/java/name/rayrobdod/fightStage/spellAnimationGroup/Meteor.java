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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.PathElements;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * 
 */
public final class Meteor implements SpellAnimationGroup {
	private static final double skyPanDistance = 200;
	
	private static final double backdropWidth = 1500;
	private static final double backdropY = -700;
	private static final double backdropHeight = 700;
	
	private static final int backLayerStreakCount = 24;
	private static final double backLayerMeteorRadius = 2.5;
	private static final double backLayerStreakLength = 60;
	private static final double backLayerStreakLengthVariant = 20;
	private static final double backLayerStreakAngle = Math.PI * 0.6;
	private static final double backLayerStreakAngleVariant = Math.PI * 0.1;
	private static final Duration backLayerStreakTime = Duration.seconds(0.4);
	
	private static final double objectFrontCoreRadius = 25;
	
	private static final Duration backdropFadeInTime = Duration.seconds(0.8);
	private static final Duration backdropFadeOutTime = Duration.seconds(0.4);
	
	private static final Duration objectFrontTime = Duration.seconds(1.6);
	
	private final Group backLayer;
	private final Group frontLayer;
	private final DoubleProperty nightBackdropHeightPercent;
	private final Rectangle nightBackdrop;
	private final List<BackgroundMeteorStreak> backLayerStreaks;
	private final ForegroundMeteor objectFrontMeteor;
	
	public Meteor() {
		this.nightBackdropHeightPercent = new SimpleDoubleProperty();
		this.nightBackdrop = new Rectangle(
			-backdropWidth / 2, backdropY,
			backdropWidth, backdropHeight
		);
		this.nightBackdrop.setOpacity(0.0);
		this.nightBackdrop.fillProperty().bind(new GradientDropdownBinding(this.nightBackdropHeightPercent));
		
		this.objectFrontMeteor = new ForegroundMeteor();
		
		this.backLayerStreaks = Stream
				.generate(BackgroundMeteorStreak::new)
				.limit(backLayerStreakCount)
				.collect(Collectors.toList());
		Node[] backLayerStreakNodes = this.backLayerStreaks.stream()
				.map(x -> x.getNode())
				.toArray(Node[]::new);
		
		this.backLayer = new Group(
			nightBackdrop,
			new Group(backLayerStreakNodes)
		);
		this.frontLayer = new Group(
			this.objectFrontMeteor.getNode()
		);
	}
	
	public Node objectBehindLayer() { return this.backLayer; }
	public Node objectFrontLayer() { return this.frontLayer; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final double direction = Math.signum(origin.getX() - target.getX());
		final Random rng = new Random();
		final Point2D vector = target.subtract(origin);
		
		final Animation backdropFadeIn = Animations.simpleAnimation(backdropFadeInTime, nightBackdrop.opacityProperty(), 0.0, 1.0);
		final Animation backdropFadeIn2 = Animations.simpleAnimation(backdropFadeInTime, nightBackdropHeightPercent, 0.0, 0.7);
		final Animation backdropFadeOut = Animations.simpleAnimation(backdropFadeOutTime, nightBackdrop.opacityProperty(), 1.0, 0.0);
		
		final Animation streakAnims = new ParallelTransition(
			Stream.iterate(0, i -> i + 1).limit(backLayerStreaks.size()).map(index -> {
				Duration time = backLayerStreakTime.divide(3).multiply(index).add(backLayerStreakTime.multiply(rng.nextDouble()));
				double x = origin.getX() +
						vector.getX() * index / backLayerStreakCount +
						(rng.nextDouble() - 0.5) * 300;
				double y = rng.nextDouble() * -150 - 300;
				BackgroundMeteorStreak streak = backLayerStreaks.get(index);
				Animation retval = streak.getAnimation(x, y, rng, direction);
				retval.setDelay(time);
				return retval;
			}).toArray(Animation[]::new)
		);
		
		return new SequentialTransition(
			new ParallelTransition(
				backdropFadeIn,
				backdropFadeIn2,
				streakAnims,
				new SequentialTransition(
					panAnimation.panToAttackerRelative(0, skyPanDistance, backdropFadeInTime),
					panAnimation.panToDefenderRelative(0, skyPanDistance, streakAnims.getCycleDuration().subtract(backdropFadeInTime))
				),
				new SequentialTransition(
					new PauseTransition(streakAnims.getCycleDuration().subtract(objectFrontTime.divide(3))),
					new ParallelTransition(
						this.objectFrontMeteor.getAnimation(target, rng, direction),
						new SequentialTransition(
							new PauseTransition(objectFrontTime.multiply(1d / 3d)),
							panAnimation.panToDefender(objectFrontTime.multiply((1d / 2d) - (1d / 3d))),
							new ParallelTransition(
								shakeAnimation.apply(12, Duration.millis(320)),
								hitAnimation
							)
						)
					)
				)
			),
			backdropFadeOut
		);
	}
	
	private static class BackgroundMeteorStreak {
		private final DoubleProperty tailPointX;
		private final DoubleProperty tailPointY;
		private final DoubleProperty headPointX;
		private final DoubleProperty headPointY;
		private final Path node;
		
		public BackgroundMeteorStreak() {
			this.tailPointX = new SimpleDoubleProperty();
			this.tailPointY = new SimpleDoubleProperty();
			this.headPointX = new SimpleDoubleProperty();
			this.headPointY = new SimpleDoubleProperty();
			
			final DoubleBinding deltaLength = new LengthBinding(tailPointX, tailPointY, headPointX, headPointY);
			final DoubleBinding deltaUnitX = headPointX.subtract(tailPointX).divide(deltaLength);
			final DoubleBinding deltaUnitY = headPointY.subtract(tailPointY).divide(deltaLength);
			final DoubleBinding x1 = new FmaBinding(-backLayerMeteorRadius, deltaUnitX, tailPointX);
			final DoubleBinding y1 = new FmaBinding(-backLayerMeteorRadius, deltaUnitY, tailPointY);
			final DoubleBinding x2 = new FmaBinding(-backLayerMeteorRadius, deltaUnitY, x1);
			final DoubleBinding y2 = new FmaBinding(backLayerMeteorRadius, deltaUnitX, y1);
			final DoubleBinding x8 = new FmaBinding(backLayerMeteorRadius, deltaUnitY, x1);
			final DoubleBinding y8 = new FmaBinding(-backLayerMeteorRadius, deltaUnitX, y1);
			final DoubleBinding x3 = new FmaBinding(-backLayerMeteorRadius, deltaUnitY, headPointX);
			final DoubleBinding y3 = new FmaBinding(backLayerMeteorRadius, deltaUnitX, headPointY);
			final DoubleBinding x7 = new FmaBinding(backLayerMeteorRadius, deltaUnitY, headPointX);
			final DoubleBinding y7 = new FmaBinding(-backLayerMeteorRadius, deltaUnitX, headPointY);
			final DoubleBinding x5 = new FmaBinding(backLayerMeteorRadius, deltaUnitX, headPointX);
			final DoubleBinding y5 = new FmaBinding(backLayerMeteorRadius, deltaUnitY, headPointY);
			final DoubleBinding x4 = new FmaBinding(-backLayerMeteorRadius, deltaUnitY, x5);
			final DoubleBinding y4 = new FmaBinding(backLayerMeteorRadius, deltaUnitX, y5);
			final DoubleBinding x6 = new FmaBinding(backLayerMeteorRadius, deltaUnitY, x5);
			final DoubleBinding y6 = new FmaBinding(-backLayerMeteorRadius, deltaUnitX, y5);
			
			this.node = new Path(
				PathElements.newBoundMoveTo(x1, y1),
				PathElements.newBoundQuadCurveTo(x2, y2, x3, y3),
				PathElements.newBoundQuadCurveTo(x4, y4, x5, y5),
				PathElements.newBoundQuadCurveTo(x6, y6, x7, y7),
				PathElements.newBoundQuadCurveTo(x8, y8, x1, y1)
			);
		}
		
		private static class LengthBinding extends DoubleBinding {
			private final ObservableDoubleValue x1;
			private final ObservableDoubleValue x2;
			private final ObservableDoubleValue y1;
			private final ObservableDoubleValue y2;
			
			public LengthBinding(ObservableDoubleValue x1, ObservableDoubleValue y1,
					ObservableDoubleValue x2, ObservableDoubleValue y2
			) {
				this.x1 = x1;
				this.x2 = x2;
				this.y1 = y1;
				this.y2 = y2;
				super.bind(x1, y1, x2, y2);
			}
			@Override protected double computeValue() {
				final double dx = x1.get() - x2.get();
				final double dy = y1.get() - y2.get();
				return Math.sqrt(dx * dx + dy * dy);
			}
		}
		
		public Node getNode() { return this.node; }
		
		public Animation getAnimation(double initX, double initY, Random rng, double directionX) {
			final double len = rng.nextDouble() * backLayerStreakLengthVariant + backLayerStreakLength;
			final double angle = rng.nextDouble() * backLayerStreakAngleVariant + backLayerStreakAngle;
			
			final double endX = initX + directionX * len * Math.cos(angle);
			final double endY = initY + len * Math.sin(angle);
			
			final double hue = rng.nextDouble() * 360;
			
			return new Timeline(
				new KeyFrame(Duration.ZERO,
					new KeyValue(this.node.fillProperty(), Color.hsb(hue, 0.2, 0.9), Interpolator.DISCRETE),
					new KeyValue(tailPointX, initX, Interpolator.LINEAR),
					new KeyValue(tailPointY, initY, Interpolator.LINEAR),
					new KeyValue(headPointX, initX, Interpolator.LINEAR),
					new KeyValue(headPointY, initY, Interpolator.LINEAR)
				),
				new KeyFrame(backLayerStreakTime.divide(2),
					new KeyValue(this.node.fillProperty(), Color.hsb(hue, 0.2, 0.9), Interpolator.LINEAR),
					new KeyValue(tailPointX, initX, Interpolator.LINEAR),
					new KeyValue(tailPointY, initY, Interpolator.LINEAR),
					new KeyValue(headPointX, endX, Interpolator.LINEAR),
					new KeyValue(headPointY, endY, Interpolator.LINEAR)
				),
				new KeyFrame(backLayerStreakTime,
					new KeyValue(this.node.fillProperty(), Color.hsb(hue, 0.2, 0.4), Interpolator.LINEAR),
					new KeyValue(tailPointX, endX, Interpolator.LINEAR),
					new KeyValue(tailPointY, endY, Interpolator.LINEAR),
					new KeyValue(headPointX, endX, Interpolator.LINEAR),
					new KeyValue(headPointY, endY, Interpolator.LINEAR)
				)
			);
		}
	}
	
	private static class ForegroundMeteor {
		private final Group node;
		private final ObjectProperty<Paint> coreColorProp;
		private final DoubleProperty coreXProp;
		private final DoubleProperty coreYProp;
		private final DoubleProperty streakLength;
		private final DoubleProperty streakAngle;
		private final DoubleProperty directionX;
		
		public ForegroundMeteor() {
			final Circle core = new Circle(0, 10000, objectFrontCoreRadius);
			this.coreColorProp = core.fillProperty();
			this.coreXProp = core.centerXProperty();
			this.coreYProp = core.centerYProperty();
			this.streakLength = new SimpleDoubleProperty();
			this.streakAngle = new SimpleDoubleProperty();
			this.directionX = new SimpleDoubleProperty(1.0);
			
			final Path wake1 = newWake(coreXProp, coreYProp, objectFrontCoreRadius + 2, 2.5, streakLength, streakAngle, directionX);
			wake1.setFill(Color.hsb(50, 0.4, 0.9, 0.9));
			wake1.setStroke(Color.TRANSPARENT);
			final Path wake2 = newWake(coreXProp, coreYProp, objectFrontCoreRadius + 5, 10, streakLength.add(50), streakAngle, directionX);
			wake2.setFill(Color.hsb(25, 0.6, 0.6, 0.6));
			wake2.setStroke(Color.TRANSPARENT);
			
			this.node = new Group(wake2, wake1, core);
		}
		
		public Node getNode() { return this.node; }
		
		public Animation getAnimation(Point2D target, Random rng, double directionX) {
			final double len = 500;
			final double angle = rng.nextDouble() * backLayerStreakAngleVariant + backLayerStreakAngle;
			final double hue = rng.nextDouble() * 360;
			
			final double targetX = target.getX();
			final double targetY = target.getY();
			final double startX = targetX - directionX * len * Math.cos(angle);
			final double startY = targetY - len * Math.sin(angle);
			final double endX = targetX + directionX * len * Math.cos(angle);
			final double endY = targetY + len * Math.sin(angle);
			
			final List<KeyFrame> keyFrames = new ArrayList<>();
			keyFrames.add(new KeyFrame(Duration.ZERO,
				new KeyValue(coreColorProp, Color.hsb(70, 0.05, 0.95), Interpolator.DISCRETE),
				new KeyValue(this.directionX, directionX, Interpolator.LINEAR),
				new KeyValue(streakLength, 80, Interpolator.LINEAR),
				new KeyValue(streakAngle, angle, Interpolator.LINEAR),
				new KeyValue(coreXProp, startX, Interpolator.LINEAR),
				new KeyValue(coreYProp, startY, Interpolator.LINEAR)
			));
			keyFrames.add(new KeyFrame(objectFrontTime,
				new KeyValue(coreColorProp, Color.hsb(70, 0.2, 0.95), Interpolator.DISCRETE),
				new KeyValue(this.directionX, directionX, Interpolator.LINEAR),
				new KeyValue(streakLength, 80, Interpolator.LINEAR),
				new KeyValue(streakAngle, angle, Interpolator.LINEAR),
				new KeyValue(coreXProp, endX, Interpolator.LINEAR),
				new KeyValue(coreYProp, endY, Interpolator.LINEAR)
			));
			
			return new Timeline(keyFrames.stream().toArray(KeyFrame[]::new));
		}
		
		private static Path newWake(ObservableDoubleValue headCx, ObservableDoubleValue headCy,
				double headRadius, double tailRadius,
				ObservableDoubleValue streakLength, ObservableDoubleValue streakAngle,
				ObservableDoubleValue directionX
		) {
			final DoubleBinding streakUnitX = new CosBinding(streakAngle).multiply(directionX);
			final DoubleBinding streakUnitY = new SinBinding(streakAngle);
			final DoubleBinding tailCx = new FmaBinding(DoubleExpression.doubleExpression(streakLength).negate(), streakUnitX, headCx);
			final DoubleBinding tailCy = new FmaBinding(DoubleExpression.doubleExpression(streakLength).negate(), streakUnitY, headCy);
			
			final DoubleBinding x0 = new FmaBinding(-tailRadius, streakUnitX, tailCx);
			final DoubleBinding y0 = new FmaBinding(-tailRadius, streakUnitY, tailCy);
			final DoubleBinding x1 = new FmaBinding(tailRadius, streakUnitY, x0);
			final DoubleBinding y1 = new FmaBinding(-tailRadius, streakUnitX, y0);
			final DoubleBinding x9 = new FmaBinding(-tailRadius, streakUnitY, x0);
			final DoubleBinding y9 = new FmaBinding(tailRadius, streakUnitX, y0);
			
			final DoubleBinding x3 = new FmaBinding(headRadius, streakUnitY, headCx);
			final DoubleBinding y3 = new FmaBinding(-headRadius, streakUnitX, headCy);
			final DoubleBinding x7 = new FmaBinding(-headRadius, streakUnitY, headCx);
			final DoubleBinding y7 = new FmaBinding(headRadius, streakUnitX, headCy);
			final DoubleBinding x5 = new FmaBinding(headRadius, streakUnitX, headCx);
			final DoubleBinding y5 = new FmaBinding(headRadius, streakUnitY, headCy);
			final DoubleBinding x4 = new FmaBinding(headRadius, streakUnitY, x5);
			final DoubleBinding y4 = new FmaBinding(-headRadius, streakUnitX, y5);
			final DoubleBinding x6 = new FmaBinding(-headRadius, streakUnitY, x5);
			final DoubleBinding y6 = new FmaBinding(headRadius, streakUnitX, y5);
			
			final DoubleBinding x2 = new FmaBinding(-headRadius, streakUnitX, x3);
			final DoubleBinding y2 = new FmaBinding(-headRadius, streakUnitY, y3);
			final DoubleBinding x8 = new FmaBinding(-headRadius, streakUnitX, x7);
			final DoubleBinding y8 = new FmaBinding(-headRadius, streakUnitY, y7);
			
			return new Path(
				PathElements.newBoundMoveTo(x0, y0),
				PathElements.newBoundCubicCurveTo(x1, y1, x2, y2, x3, y3),
				PathElements.newBoundQuadCurveTo(x4, y4, x5, y5),
				PathElements.newBoundQuadCurveTo(x6, y6, x7, y7),
				PathElements.newBoundCubicCurveTo(x8, y8, x9, y9, x0, y0)
			);
		}
	}
	
	private static class GradientDropdownBinding extends ObjectBinding<LinearGradient> {
		private final ObservableDoubleValue stopPercent;
		public GradientDropdownBinding(ObservableDoubleValue stopPercent) {
			this.stopPercent = stopPercent;
			super.bind(stopPercent);
		}
		@Override protected LinearGradient computeValue() {
			return new LinearGradient(
				0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
				new Stop(0, Color.gray(0, 1)),
				new Stop(this.stopPercent.get(), Color.gray(0, 0.9)),
				new Stop(1, Color.TRANSPARENT)
			);
		}
	}
	
	private static class FmaBinding extends DoubleBinding {
		private final ObservableDoubleValue a;
		private final ObservableDoubleValue b;
		private final ObservableDoubleValue c;
		public FmaBinding(ObservableDoubleValue a, ObservableDoubleValue b, ObservableDoubleValue c) {
			this.a = a;
			this.b = b;
			this.c = c;
			super.bind(a, b, c);
		}
		public FmaBinding(double a, ObservableDoubleValue b, ObservableDoubleValue c) {
			this(new SimpleDoubleProperty(a), b, c);
		}
		@Override protected double computeValue() {
			return a.get() * b.get() + c.get();
		}
	}
	
	private static class SinBinding extends DoubleBinding {
		private final ObservableDoubleValue a;
		public SinBinding(ObservableDoubleValue a) {
			this.a = a;
			super.bind(a);
		}
		@Override protected double computeValue() {
			return Math.sin(a.get());
		}
	}
	
	private static class CosBinding extends DoubleBinding {
		private final ObservableDoubleValue a;
		public CosBinding(ObservableDoubleValue a) {
			this.a = a;
			super.bind(a);
		}
		@Override protected double computeValue() {
			return Math.cos(a.get());
		}
	}
}
