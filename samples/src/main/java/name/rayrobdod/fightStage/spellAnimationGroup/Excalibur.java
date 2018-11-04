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
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
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
	
	private static final Duration fadeToBlackDur = Duration.seconds(1.1);
	private static final Duration pauseBlackDur = Duration.seconds(0.5);
	private static final Duration fadeToBurstDur = Duration.seconds(2.0);
	private static final Duration pauseBurstDur = Duration.seconds(4.0);
	private static final Duration explodeDur = Duration.seconds(2.2);
	private static final Duration pauseWhiteDur = Duration.seconds(0.8); // damage somewhere in here
	private static final Duration fadeToNormalDur = Duration.seconds(0.5);
	
	private static final Duration fadeToBlackStartTime = Duration.ZERO;
	private static final Duration fadeToBlackEndTime = fadeToBlackStartTime.add(fadeToBlackDur);	private static final Duration pauseToBlackStartTime = fadeToBlackEndTime;
	private static final Duration pauseToBlackEndTime = pauseToBlackStartTime.add(pauseBlackDur);	private static final Duration fadeToBurstStartTime = pauseToBlackEndTime;
	private static final Duration fadeToBurstEndTime = fadeToBurstStartTime.add(fadeToBurstDur);
	private static final Duration pauseBurstStartTime = fadeToBurstEndTime;
	private static final Duration pauseBurstEndTime = pauseBurstStartTime.add(pauseBurstDur);
	private static final Duration explodeStartTime = pauseBurstEndTime.subtract(explodeDur);
	private static final Duration explodeEndTime = pauseBurstEndTime;
	private static final Duration pauseWhiteStartTime = explodeEndTime;
	private static final Duration pauseWhiteEndTime = explodeEndTime.add(pauseWhiteDur);
	private static final Duration fadeToNormalStartTime = pauseWhiteEndTime;
	private static final Duration fadeToNormalEndTime = pauseWhiteEndTime.add(fadeToNormalDur);
	
	private static final int framesPerSecond = 16;
	private static final int gradientTransformPrecision = 100;
	private static final int gradientPrecision = 100;
	private static final int backgroundDimension = 100;
	private static final int explodeSize = 400;
	
	private static final Duration totalDuration = fadeToNormalEndTime;
	private static final int gradientFrames = (int) (totalDuration.toSeconds() * framesPerSecond);
	
	private final Scale backgroundScale;
	private final LinearGradient[] horizontalGradients;
	private final LinearGradient[] verticalGradients;
	private final Rectangle horizontalGradientRect;
	private final Rectangle verticalGradientRect;
	private final Rectangle blackRect;
	private final Node gradientsGroup;
	private final Node background;
	
	private final Node backLayer;
	
	private final Rectangle whiteRect;
	private final Node frontLayer;
	
	public Excalibur() {
		this.horizontalGradients = new LinearGradient[gradientFrames];
		this.verticalGradients = new LinearGradient[gradientFrames];
		this.initializeGradients(new Random());
		
		this.blackRect = bufferedRectangle();
		this.horizontalGradientRect = bufferedRectangle();
		this.verticalGradientRect = bufferedRectangle();
		this.verticalGradientRect.setBlendMode(BlendMode.OVERLAY);
		this.whiteRect = bufferedRectangle();
		this.backgroundScale = new Scale();
		
		this.gradientsGroup = new Group(
			this.horizontalGradientRect,
			this.verticalGradientRect
		);
		this.background = new Group(
			this.blackRect,
			this.gradientsGroup,
			this.whiteRect
		);
		this.background.getTransforms().add(this.backgroundScale);
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
		
		final Timeline timeline = new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(backgroundScale.xProperty(), direction * 1d / backgroundDimension, Interpolator.DISCRETE),
				new KeyValue(backgroundScale.yProperty(), 1d / backgroundDimension, Interpolator.DISCRETE),
				new KeyValue(backgroundScale.pivotXProperty(), direction > 0 ? 0 : 1, Interpolator.DISCRETE),
				new KeyValue(blackRect.fillProperty(), Color.TRANSPARENT, Interpolator.DISCRETE),
				new KeyValue(gradientsGroup.opacityProperty(), 0.0, Interpolator.DISCRETE),
				new KeyValue(whiteRect.fillProperty(), Color.TRANSPARENT, Interpolator.DISCRETE)
			),
			new KeyFrame(fadeToBlackEndTime,
				new KeyValue(blackRect.fillProperty(), Color.BLACK, Interpolator.LINEAR)
			),
			new KeyFrame(fadeToBurstStartTime.subtract(Duration.ONE),
				new KeyValue(gradientsGroup.opacityProperty(), 0.0, Interpolator.LINEAR)
			),
			new KeyFrame(fadeToBurstStartTime,
				new KeyValue(gradientsGroup.opacityProperty(), 1.0, Interpolator.LINEAR)
			),
			new KeyFrame(pauseBurstEndTime.subtract(Duration.ONE),
				new KeyValue(blackRect.fillProperty(), Color.BLACK, Interpolator.LINEAR),
				new KeyValue(gradientsGroup.opacityProperty(), 1.0, Interpolator.LINEAR)
			),
			new KeyFrame(pauseBurstEndTime,
				new KeyValue(blackRect.fillProperty(), Color.TRANSPARENT, Interpolator.LINEAR),
				new KeyValue(gradientsGroup.opacityProperty(), 0.0, Interpolator.LINEAR)
			),
			new KeyFrame(explodeStartTime.add(explodeEndTime).divide(2),
				new KeyValue(whiteRect.fillProperty(), Color.color(1.0,1.0,1.0,0.0), Interpolator.DISCRETE)
			),
			new KeyFrame(explodeEndTime,
				new KeyValue(whiteRect.fillProperty(), Color.WHITE, Interpolator.LINEAR)
			),
			new KeyFrame(fadeToNormalStartTime,
				new KeyValue(whiteRect.fillProperty(), Color.WHITE, Interpolator.LINEAR)
			),
			new KeyFrame(fadeToNormalEndTime,
				new KeyValue(backgroundScale.xProperty(), direction * 1d / backgroundDimension, Interpolator.DISCRETE),
				new KeyValue(backgroundScale.yProperty(), 1d / backgroundDimension, Interpolator.DISCRETE),
				new KeyValue(backgroundScale.pivotXProperty(), direction > 0 ? 0 : 1, Interpolator.DISCRETE),
				new KeyValue(whiteRect.fillProperty(), Color.color(1.0,1.0,1.0,0.0), Interpolator.LINEAR)
			)
		);
		
		
		for (int i = 0; i < gradientFrames; i++) {
			timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(((double) i) / framesPerSecond),
				new KeyValue(horizontalGradientRect.fillProperty(), horizontalGradients[i], Interpolator.DISCRETE),
				new KeyValue(verticalGradientRect.fillProperty(), verticalGradients[i], Interpolator.DISCRETE)
			));
		}
		
		
		return new ParallelTransition(
			panAnimation.panToDefender(),
			timeline,
			new SequentialTransition(
				new PauseTransition(pauseWhiteStartTime.add(pauseWhiteDur.divide(3))),
				new ParallelTransition(
					shakeAnimation.apply(),
					hitAnimation
				)
			)
		);
	}
	
	
	
	private void initializeGradients(Random rng) {
		final double fadeToBurstStartTimeSecs = fadeToBurstStartTime.toSeconds();
		final double fadeToBurstEndTimeSecs = fadeToBurstEndTime.toSeconds();
		final PerlinNoise horizontalNoise = new PerlinNoise(rng);
		final PerlinNoise verticalNoise = new PerlinNoise(rng);
		
		for (int t = 0; t < gradientFrames; t++) {
			final double t2 = ((double) t) / framesPerSecond;
			final double fadeInOffset = (
				t2 <= fadeToBurstStartTimeSecs ? -0.1 : (
					t2 <= fadeToBurstEndTimeSecs ? (t2 - fadeToBurstStartTimeSecs) / (fadeToBurstEndTimeSecs - fadeToBurstStartTimeSecs) : (
						1
					)
				)
			) - 0.1;
			
			List<Stop> horizontalGradientStops = new ArrayList<>(gradientPrecision);
			for (int x = 0; x <= gradientPrecision; x++) {
				double x2 = ((double) x) / gradientPrecision;
				double lum = fadeInOffset + horizontalNoise.sum1D((x2 - t2 / 8) * 16, 2, 2, 4) * 2;
				double colorG = Math.min(0.95f, Math.max(0.05f, lum));
				double colorB = Math.min(0.95, Math.max(0.05f, lum - 1));
				Color color = Color.color(colorB, colorG, (colorB + colorG) / 2);
				
				horizontalGradientStops.add(new Stop(x2, color));
			}
			horizontalGradients[t] = new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE, horizontalGradientStops);
			
			
			List<Stop> verticalGradientStops = new ArrayList<>(gradientPrecision);
			for (int y = 0; y <= gradientPrecision; y++) {
				double y2 = ((double) y) / gradientPrecision;
				double lum = fadeInOffset + verticalNoise.sum1D(y2 * 32, 2, 2, 4) * 2;
				double colorY = Math.min(0.95f, Math.max(0.05f, lum));
				double colorB = Math.min(0.95, Math.max(0.05f, lum - 1));
				Color color = Color.color(colorY, colorY, colorB);
				
				verticalGradientStops.add(new Stop(y2, color));
			}
			verticalGradients[t] = new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE, verticalGradientStops);
		}
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
}
