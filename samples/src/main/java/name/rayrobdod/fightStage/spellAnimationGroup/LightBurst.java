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
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A spell effect using a background.
 * 
 * Slightly inspired by FE4's Nara tome's visuals.
 */
public final class LightBurst implements SpellAnimationGroup {
	
	private static final Duration fadeToBlackDur = Duration.seconds(1.1);
	private static final Duration pauseBlackDur = Duration.seconds(0.5);
	private static final Duration fadeToBurstDur = Duration.seconds(2.0);
	private static final Duration pauseBurstDur = Duration.seconds(4.0);
	private static final Duration explodeDur = Duration.seconds(2.2);
	private static final Duration pauseWhiteDur = Duration.seconds(0.8); // damage somewhere in here
	private static final Duration fadeToNormalDur = Duration.seconds(0.5);
	
	private static final Duration fadeToBlackStartTime = Duration.ZERO; 
	private static final Duration fadeToBlackEndTime = fadeToBlackStartTime.add(fadeToBlackDur); 
	private static final Duration pauseToBlackStartTime = fadeToBlackEndTime;
	private static final Duration pauseToBlackEndTime = pauseToBlackStartTime.add(pauseBlackDur); 
	private static final Duration fadeToBurstStartTime = pauseToBlackEndTime;
	private static final Duration fadeToBurstEndTime = fadeToBurstStartTime.add(fadeToBurstDur);
	private static final Duration pauseBurstStartTime = fadeToBurstEndTime;
	private static final Duration pauseBurstEndTime = pauseBurstStartTime.add(pauseBurstDur);
	private static final Duration explodeStartTime = pauseBurstEndTime.subtract(explodeDur);
	private static final Duration explodeEndTime = pauseBurstEndTime;
	private static final Duration pauseWhiteStartTime = explodeEndTime;
	private static final Duration pauseWhiteEndTime = explodeEndTime.add(pauseWhiteDur);
	private static final Duration fadeToNormalStartTime = pauseWhiteEndTime;
	private static final Duration fadeToNormalEndTime = pauseWhiteEndTime.add(fadeToNormalDur);
	
	private static final int framesPerSecond = 8;
	private static final int polarTransformPrecision = 100;
	private static final int gradientPrecision = 100;
	private static final int backgroundWidth = 500;
	private static final int backgroundHeight = 500;
	private static final int explodeSize = 400;
	
	private static final Duration totalDuration = fadeToNormalEndTime;
	private static final int gradientFrames = (int) (totalDuration.toSeconds() * framesPerSecond);
	
	private final LinearGradient[] horizontalGradients;
	private final LinearGradient[] verticalGradients;
	private final Rectangle horizontalGradientRect;
	private final Rectangle verticalGradientRect;
	private final Rectangle blackRect;
	private final Node gradientsGroup;
	private final Node background;
	
	private final Rectangle whiteRect;
	private final MoveTo explodeShape1;
	private final CubicCurveTo explodeShape2;
	private final CubicCurveTo explodeShape3;
	private final Node foreground;
	
	public LightBurst() {
		this.horizontalGradients = new LinearGradient[gradientFrames];
		this.verticalGradients = new LinearGradient[gradientFrames];
		this.initializeGradients(new Random());
		
		this.blackRect = new Rectangle();
		this.horizontalGradientRect = new Rectangle();
		this.verticalGradientRect = new Rectangle();
		this.verticalGradientRect.setBlendMode(BlendMode.OVERLAY);
		
		this.gradientsGroup = new Group(
			this.horizontalGradientRect,
			this.verticalGradientRect
		);
		this.background = new Group(
			this.blackRect,
			this.gradientsGroup
		);
		this.background.setEffect(polarTransform());
		
		
		this.whiteRect = new Rectangle();
		this.explodeShape1 = new MoveTo();
		this.explodeShape2 = new CubicCurveTo();
		this.explodeShape3 = new CubicCurveTo();
		final Path explodeShape = new Path(explodeShape1, explodeShape2, explodeShape3);
		explodeShape.setFill(Color.WHITE);
		explodeShape.setStroke(Color.TRANSPARENT);
		this.foreground = new Group(
			explodeShape,
			this.whiteRect
		);
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final double backgroundX = (origin.getX() + target.getX()) / 2 - backgroundWidth / 2;
		final double backgroundY = (origin.getY() + target.getY()) / 2 - backgroundHeight / 2;
		final Point2D explosionCenter = new Point2D(target.getX(), GROUND_Y);
		
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(blackRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(blackRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(blackRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(blackRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(whiteRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(whiteRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(whiteRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(whiteRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(blackRect.fillProperty(), Color.TRANSPARENT, Interpolator.DISCRETE),
			new KeyValue(gradientsGroup.opacityProperty(), 0.0, Interpolator.DISCRETE),
			new KeyValue(whiteRect.fillProperty(), Color.TRANSPARENT, Interpolator.DISCRETE),
			new KeyValue(explodeShape1.xProperty(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.xProperty(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.xProperty(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlX1Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlX1Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlX2Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlX2Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape1.yProperty(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.yProperty(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.yProperty(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlY1Property(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlY1Property(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlY2Property(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlY2Property(), explosionCenter.getY(), Interpolator.DISCRETE)
		));
		// Timeline apparently will not touch something without it being mentioned at least twice
		timeline.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(blackRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(blackRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(blackRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(blackRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(whiteRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(whiteRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(whiteRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(whiteRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(horizontalGradientRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(verticalGradientRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToBlackEndTime,
			new KeyValue(blackRect.fillProperty(), Color.BLACK, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToBurstStartTime.subtract(Duration.ONE),
			new KeyValue(gradientsGroup.opacityProperty(), 0.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToBurstStartTime,
			new KeyValue(gradientsGroup.opacityProperty(), 1.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(pauseBurstEndTime.subtract(Duration.ONE),
			new KeyValue(blackRect.fillProperty(), Color.BLACK, Interpolator.LINEAR),
			new KeyValue(gradientsGroup.opacityProperty(), 1.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(pauseBurstEndTime,
			new KeyValue(blackRect.fillProperty(), Color.TRANSPARENT, Interpolator.LINEAR),
			new KeyValue(gradientsGroup.opacityProperty(), 0.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(explodeStartTime,
			new KeyValue(explodeShape1.xProperty(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.xProperty(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.xProperty(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlX1Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlX1Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlX2Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlX2Property(), explosionCenter.getX(), Interpolator.DISCRETE),
			new KeyValue(explodeShape1.yProperty(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.yProperty(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.yProperty(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlY1Property(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlY1Property(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlY2Property(), explosionCenter.getY(), Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlY2Property(), explosionCenter.getY(), Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(explodeStartTime.add(explodeEndTime).divide(2),
			new KeyValue(whiteRect.fillProperty(), Color.color(1.0,1.0,1.0,0.0), Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(explodeEndTime,
			new KeyValue(whiteRect.fillProperty(), Color.WHITE, Interpolator.LINEAR),
			new KeyValue(explodeShape1.xProperty(), explosionCenter.getX() - explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape2.controlX1Property(), explosionCenter.getX() - explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape2.controlX2Property(), explosionCenter.getX() + explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape2.xProperty(), explosionCenter.getX() + explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape3.controlX1Property(), explosionCenter.getX()+ explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape3.controlX2Property(), explosionCenter.getX()- explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape3.xProperty(), explosionCenter.getX() - explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape1.yProperty(), explosionCenter.getY(), Interpolator.LINEAR),
			new KeyValue(explodeShape2.controlY1Property(), explosionCenter.getY() - explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape2.controlY2Property(), explosionCenter.getY() - explodeSize, Interpolator.LINEAR),
			new KeyValue(explodeShape2.yProperty(), explosionCenter.getY(), Interpolator.LINEAR),
			new KeyValue(explodeShape3.controlY1Property(), explosionCenter.getY() + explodeSize / 3, Interpolator.LINEAR),
			new KeyValue(explodeShape3.controlY2Property(), explosionCenter.getY() + explodeSize / 3, Interpolator.LINEAR),
			new KeyValue(explodeShape3.yProperty(), explosionCenter.getY(), Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToNormalStartTime,
			new KeyValue(whiteRect.fillProperty(), Color.WHITE, Interpolator.LINEAR),
			new KeyValue(explodeShape1.xProperty(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlX1Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlX2Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape2.xProperty(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlX1Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlX2Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape3.xProperty(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape1.yProperty(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlY1Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape2.controlY2Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape2.yProperty(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlY1Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape3.controlY2Property(), 0, Interpolator.DISCRETE),
			new KeyValue(explodeShape3.yProperty(), 0, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToNormalEndTime,
			new KeyValue(whiteRect.fillProperty(), Color.TRANSPARENT, Interpolator.LINEAR)
		));
		
		
		for (int i = 0; i < gradientFrames; i++) {
			timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(((double) i) / framesPerSecond),
				new KeyValue(horizontalGradientRect.fillProperty(), horizontalGradients[i], Interpolator.DISCRETE),
				new KeyValue(verticalGradientRect.fillProperty(), verticalGradients[i], Interpolator.DISCRETE)
			));
		}
		
		
		return new ParallelTransition(
			panAnimation,
			timeline,
			new SequentialTransition(
				new PauseTransition(pauseWhiteStartTime.add(pauseWhiteDur.divide(3))),
				hpAndShakeAnimation
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
				double colorY = Math.min(0.95f, Math.max(0.05f, lum));
				double colorB = Math.min(0.95, Math.max(0.05f, lum - 1));
				Color color = Color.color(colorY, colorY, colorB);
				
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
	private static DisplacementMap polarTransform() {
		final FloatMap floatMap = new FloatMap();
		floatMap.setWidth(polarTransformPrecision);
		floatMap.setHeight(polarTransformPrecision);
		
		for (int i = 0; i < floatMap.getWidth(); i++) {
			for (int j = 0; j < floatMap.getHeight(); j++) {
				double centerX = 0.5;
				double centerY = 0.5;
				double currentX = (0.5 + i) / floatMap.getWidth();
				double currentY = (0.5 + j) / floatMap.getHeight();
				
				double r = Math.hypot(currentX - centerX, currentY - centerY) * Math.sqrt(2);
				double theta = Math.atan2(currentX - centerX, currentY - centerY) / Math.PI / 2 + 0.5;
				
				assert r <= 1.0 && r >= 0.0 && theta <= 1.0 && theta >= 0.0 : "r = " + r + "; ϴ = " + theta;
				
				double offsetX = r - currentX;
				double offsetY = theta - currentY;
				
				floatMap.setSamples(i, j, (float) offsetX, (float) offsetY);
			}
		}
		
		DisplacementMap retval = new DisplacementMap();
		retval.setMapData(floatMap);
		return retval;
	}
}
