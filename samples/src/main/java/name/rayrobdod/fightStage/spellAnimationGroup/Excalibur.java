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
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableObjectValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 */
// Gjallarhorn(?)
public final class Excalibur implements SpellAnimationGroup {
	
	private static final Duration fadeToBurstDur = Duration.seconds(2.0);
	private static final Duration pauseBurst1Dur = Duration.seconds(2.5);
	private static final Duration pauseBurst2Dur = Duration.seconds(1.0);
	private static final Duration fadeOutBurstDur = Duration.seconds(0.5);
	
	
	private static final int framesPerSecond = 16;
	private static final int gradientTransformPrecision = 64;
	private static final int gradientPrecision = 128;
	private static final int backgroundDimension = 100;
	
	private final PerlinNoise horizontalNoise;
	private final PerlinNoise verticalNoise;
	
	private final WritableDoubleValue backgroundDirectionXProperty;
	private final WritableDoubleValue gradientsOpacityProperty;
	private final WritableObjectValue<Paint> horizFillProperty;
	private final WritableObjectValue<Paint> vertFillProperty;
	
	private final Node background;
	private final Node backLayer;
	private final Node frontLayer;
	
	public Excalibur() {
		final Random rng = new Random();
		this.horizontalNoise = new PerlinNoise(rng);
		this.verticalNoise = new PerlinNoise(rng);
		
		final Rectangle horizGradientRect = bufferedRectangle();
		final Rectangle vertGradientRect = bufferedRectangle();
		vertGradientRect.setBlendMode(BlendMode.OVERLAY);
		final Scale backgroundScale = new Scale();
		backgroundScale.setPivotX(0.5);
		
		this.backgroundDirectionXProperty = backgroundScale.xProperty();
		
		this.horizFillProperty = horizGradientRect.fillProperty();
		this.vertFillProperty = vertGradientRect.fillProperty();
		
		
		final Node gradientsGroup = new Group(
			horizGradientRect,
			vertGradientRect
		);
		this.gradientsOpacityProperty = gradientsGroup.opacityProperty();
		
		this.background = new Group(
			gradientsGroup
		);
		this.background.getTransforms().add(backgroundScale);
		this.background.getTransforms().add(new Scale(1d / backgroundDimension, 1d / backgroundDimension));
		this.background.setEffect(gradientTransform());
		
		this.frontLayer = new Group();
		this.backLayer = new Group();
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
		final double direction = Math.signum(target.getX() - origin.getX());
		
		final TimelineBuilder builder = new TimelineBuilder();
		
		builder.setBackgroundDirectionX(direction);
		builder.setGradientsOpacity(0.0);
		builder.stampFrame();
		
		builder.incrementTime(fadeToBurstDur);
		builder.setGradientsOpacity(1.0);
		builder.stampFrame();
		
		builder.incrementTime(pauseBurst1Dur);
		builder.stampFrame();
		final Timeline beforeHit = builder.build();
		
		builder.resetTime();
		builder.clearTimeline();
		builder.stampFrame();
		
		builder.incrementTime(pauseBurst2Dur);
		builder.stampFrame();
		
		builder.incrementTime(fadeOutBurstDur);
		builder.setGradientsOpacity(0.0);
		builder.stampFrame();
		final Timeline afterHit = builder.build();
		
		
		// Create the gradient frames
		{
			final int beforeHitStartIndex = 0;
			final int beforeHitEndIndex = beforeHitStartIndex + (int) Math.ceil(fadeToBurstDur.add(pauseBurst1Dur).toSeconds() * framesPerSecond);
			final int afterHitStartIndex = 0;
			final int afterHitEndIndex = (int) Math.floor(pauseBurst2Dur.add(fadeOutBurstDur).toSeconds() * framesPerSecond);
			
			for (int i = beforeHitStartIndex; i < beforeHitEndIndex; i++) {
				final double secs = ((double) i) / framesPerSecond;
				final Duration dur = Duration.seconds(secs);
				
				beforeHit.getKeyFrames().add(new KeyFrame(dur,
					new KeyValue(horizFillProperty, createGradient(true, secs), Interpolator.DISCRETE),
					new KeyValue(vertFillProperty, createGradient(false, secs), Interpolator.DISCRETE)
				));
			}
			
			for (int i = afterHitStartIndex; i < afterHitEndIndex; i++) {
				final double offsetSecs = ((double) beforeHitEndIndex) / framesPerSecond;
				final double secs = ((double) i) / framesPerSecond;
				final Duration dur = Duration.seconds(secs);
				
				afterHit.getKeyFrames().add(new KeyFrame(dur,
					new KeyValue(horizFillProperty, createGradient(true, secs + offsetSecs), Interpolator.DISCRETE),
					new KeyValue(vertFillProperty, createGradient(false, secs + offsetSecs), Interpolator.DISCRETE)
				));
			}
		}
		
		
		return new ParallelTransition(
			panAnimation.panToDefender(),
			new SequentialTransition(
				beforeHit,
				new ParallelTransition(
					shakeAnimation.apply(),
					hitAnimation,
					afterHit
				)
			)
		);
	}
	
	
	private LinearGradient createGradient(boolean useHoriz, double time) {
		final PerlinNoise noise = (useHoriz ? horizontalNoise : verticalNoise);
		
		final ArrayList<Stop> gradientStops = new ArrayList<>(gradientPrecision);
		for (int x = 0; x <= gradientPrecision; x++) {
			double x2 = ((double) x) / gradientPrecision;
			double lum = 0.9 + horizontalNoise.sum1D(useHoriz ? (x2 - time * Math.max(0.5f, time) / 16) * 16 : x2 * 32, 2, 2, 4) * 2;
			double colorG = Math.min(0.95f, Math.max(0.05f, lum));
			double colorB = Math.min(0.95, Math.max(0.05f, lum - 1));
			Color color = Color.color(colorB, colorG, (colorB + colorG) / 2);
			
			gradientStops.add(new Stop(x2, color));
		}
		return new LinearGradient(0, 0, (useHoriz ? 1 : 0), (useHoriz ? 0 : 1), true, CycleMethod.NO_CYCLE, gradientStops);
	}
	
	/**
	 * Returns a DisplacementMap which when applied to a node transforms it into using
	 * polar coordinates.
	 */
	private static DisplacementMap gradientTransform() {
		final FloatMap floatMap = new FloatMap();
		floatMap.setWidth(gradientTransformPrecision);
		floatMap.setHeight(gradientTransformPrecision);
		
		for (int i = 0; i < floatMap.getWidth(); i++) {
			for (int j = 0; j < floatMap.getHeight(); j++) {
				double currentX = (0.5 + i) / floatMap.getWidth();
				double currentY = (0.5 + j) / floatMap.getHeight();
				
				double offsetX = Math.sin(currentY * 30) / 75;
				double offsetY = Math.sin(currentX * 10) / 75;
				
				floatMap.setSamples(i, j, (float) offsetX, (float) offsetY);
			}
		}
		
		DisplacementMap retval = new DisplacementMap();
		retval.setMapData(floatMap);
		return retval;
	}
	
	private static Rectangle bufferedRectangle() {
		Rectangle retval = new Rectangle(
			-backgroundDimension / 4,
			-backgroundDimension / 4,
			backgroundDimension * 3 / 2,
			backgroundDimension * 3 / 2);
		retval.setFill(Color.TRANSPARENT);
		return retval;
	}
	
	private final class TimelineBuilder {
		private Duration currentTime;
		
		private double backgroundDirectionX;
		private double gradientsOpacity;
		
		private final ArrayList<KeyFrame> timeline;
		
		public TimelineBuilder() {
			this.currentTime = Duration.ZERO;
			
			this.backgroundDirectionX = 1.0;
			this.gradientsOpacity = 0.0;
			
			this.timeline = new ArrayList<>();
		}
		
		/** Add the current builder state to the internal list of key frames */
		public void stampFrame() {
			timeline.add(new KeyFrame(currentTime,
				new KeyValue(backgroundDirectionXProperty, backgroundDirectionX, Interpolator.LINEAR),
				new KeyValue(gradientsOpacityProperty, gradientsOpacity, Interpolator.LINEAR)
			));
		}
		/** clear the internal list of key frames */
		public void clearTimeline() {this.timeline.clear();}
		
		public void incrementTime(Duration delta) {this.currentTime = this.currentTime.add(delta);}
		public void resetTime() {this.currentTime = Duration.ZERO;}
		
		public Timeline build() {return new Timeline(timeline.stream().toArray(KeyFrame[]::new));}
		
		
		public void setBackgroundDirectionX(double v) {this.backgroundDirectionX = v;}
		public void setGradientsOpacity(double v) {this.gradientsOpacity = v;}
	}
}
