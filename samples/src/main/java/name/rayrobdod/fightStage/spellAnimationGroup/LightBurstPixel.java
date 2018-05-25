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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A spell effect using a background.
 * 
 * Slightly inspired by FE4's Nara tome's visuals.
 */
public final class LightBurstPixel implements SpellAnimationGroup {
	
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
	private static final int backgroundWidth = 500;
	private static final int backgroundHeight = 500;
	private static final int explodeSize = 400;
	
	private static final Duration totalDuration = fadeToNormalEndTime;
	private static final int gradientFrames = (int) (totalDuration.toSeconds() * framesPerSecond);
	
	private final Image[] burstFrames;
	private final Rectangle blackRect;
	private final ImageView burstView;
	private final Node background;
	
	private final Rectangle whiteRect;
	private final MoveTo explodeShape1;
	private final CubicCurveTo explodeShape2;
	private final CubicCurveTo explodeShape3;
	private final Node foreground;
	
	public LightBurstPixel() {
		this.burstFrames = this.makeBurstFrames(new Random());
		
		this.blackRect = new Rectangle();
		this.burstView = new ImageView();
		this.background = new Group(
			this.blackRect,
			this.burstView
		);
		
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
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
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
			new KeyValue(blackRect.fillProperty(), Color.TRANSPARENT, Interpolator.LINEAR),
			new KeyValue(whiteRect.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(whiteRect.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(whiteRect.widthProperty(), backgroundWidth, Interpolator.DISCRETE),
			new KeyValue(whiteRect.heightProperty(), backgroundHeight, Interpolator.DISCRETE),
			new KeyValue(whiteRect.fillProperty(), Color.TRANSPARENT, Interpolator.DISCRETE),
			new KeyValue(burstView.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(burstView.yProperty(), backgroundY, Interpolator.DISCRETE),
			new KeyValue(burstView.opacityProperty(), 0.0, Interpolator.LINEAR),
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
			new KeyValue(burstView.xProperty(), backgroundX, Interpolator.DISCRETE),
			new KeyValue(burstView.yProperty(), backgroundY, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToBlackEndTime,
			new KeyValue(blackRect.fillProperty(), Color.BLACK, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToBurstStartTime.subtract(Duration.ONE),
			new KeyValue(burstView.opacityProperty(), 0.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeToBurstStartTime,
			new KeyValue(burstView.opacityProperty(), 1.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(pauseBurstEndTime.subtract(Duration.ONE),
			new KeyValue(blackRect.fillProperty(), Color.BLACK, Interpolator.LINEAR),
			new KeyValue(burstView.opacityProperty(), 1.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(pauseBurstEndTime,
			new KeyValue(blackRect.fillProperty(), Color.TRANSPARENT, Interpolator.LINEAR),
			new KeyValue(burstView.opacityProperty(), 0.0, Interpolator.LINEAR)
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
				new KeyValue(burstView.imageProperty(), burstFrames[i], Interpolator.DISCRETE)
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
	
	
	private final Image[] makeBurstFrames(Random rng) {
		final PerlinNoise noise = new PerlinNoise(rng);
		final Image[] retval = new Image[gradientFrames];
		
		for (int t = 0; t < gradientFrames; t++) {
			final double t2 = ((double) t) / framesPerSecond;
			retval[t] = new WritableImage(new BurstPixelReader(noise, t2), backgroundWidth, backgroundHeight);
		}
		return retval;
	}
	
	/**
	 * A PixelReader that afsd
	 */
	private final class BurstPixelReader implements PixelReader {
		private final PerlinNoise noise;
		private final double time;
		private final double fadeInOffset;
		private final int cx = backgroundWidth / 2;
		private final int cy = backgroundHeight / 2;
		
		public BurstPixelReader(PerlinNoise noise, double time) {
			this.noise = noise;
			this.time = time;
			
			final double fadeToBurstStartTimeSecs = fadeToBurstStartTime.toSeconds();
			final double fadeToBurstEndTimeSecs = fadeToBurstEndTime.toSeconds();
			this.fadeInOffset = (
				time <= fadeToBurstStartTimeSecs ? 0 : (
					time <= fadeToBurstEndTimeSecs ? (time - fadeToBurstStartTimeSecs) / (fadeToBurstEndTimeSecs - fadeToBurstStartTimeSecs) : (
						1
					)
				)
			) - 0.2;
		}
		
		public int getArgb(int x, int y) {
			final double radius = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
			final double angle = Math.atan2(x - cx, y - cy);
			
			final double lum = fadeInOffset + noise.sum2D(
				angle * 3 + 50,
				Math.max(radius, 7) / 40 - time * 2 + 200.2,
				2, 2, 4
			) * 1.5;
			final double colorY = Math.min(0.95f, Math.max(0.05f, lum));
			final double colorB = Math.min(0.95, Math.max(0.05f, lum - 1));
			final int colorYb = (int) (colorY * 255);
			final int colorBb = (int) (colorB * 255);
			return 0xFF000000 | colorYb << 16 | colorYb << 8 | colorBb;
		}
		
		public Color getColor(int x, int y) {
			throw new UnsupportedOperationException();
		}
		
		public javafx.scene.image.PixelFormat getPixelFormat() {
			throw new UnsupportedOperationException();
		}
		
		public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
			throw new UnsupportedOperationException();
		}
		
		public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
			throw new UnsupportedOperationException();
		}
		
		public <T extends java.nio.Buffer> void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) {
			for (int i = x; i < x + w; i++) {
				for (int j = y; j < y + h; j++) {
					pixelformat.setArgb(buffer, i, j, scanlineStride, this.getArgb(i, j));
				}
			}
		}
	}

}
