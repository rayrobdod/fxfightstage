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

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A spell animation that demonstrates the use of a spritesheet
 */
public final class RadialLightning implements SpellAnimationGroup {
	
	private static final String filename = "/name/rayrobdod/fightStage/spellAnimationGroup/radialLightning.png";
	
	private static final double sourceFrameWidth = 256;
	private static final double sourceFrameHeight = 256;
	private static final double renderWidth = 160;
	private static final double renderHeight = 160;
	private static final Duration frameLength = Duration.seconds(0.09);
	private static final int framesPerRow = 4;
	private static final Rectangle2D hiddenViewport = new Rectangle2D(0, 0, 1, 1);
	
	private final ImageView foreground;
	private final Node background;
	private final DoubleProperty targetPointXProp;
	private final DoubleProperty targetPointYProp;
	
	public RadialLightning() {
		final Translate centerToZeroTranslate = new Translate(
			-renderWidth / 2,
			-renderHeight / 2
		);
		final Translate targetPointTranslate = new Translate();
		this.targetPointXProp = targetPointTranslate.xProperty();
		this.targetPointYProp = targetPointTranslate.yProperty();
		
		final Image img = new Image(filename);
		this.foreground = new ImageView(img);
		this.foreground.getTransforms().add(centerToZeroTranslate);
		this.foreground.getTransforms().add(targetPointTranslate);
		this.foreground.setViewport(hiddenViewport);
		this.foreground.setFitWidth(renderWidth);
		this.foreground.setFitHeight(renderHeight);
		
		this.background = new Group();
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	@Override
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Timeline beforeShakeAnim = new Timeline();
		final Timeline afterShakeAnim = new Timeline();
		for (int i = 0; i < framesPerRow; i++) {
			final Duration time = frameLength.multiply(i);
			final double x = sourceFrameWidth * i;
			final double beforeY = 0;
			final double afterY = sourceFrameHeight;
			
			beforeShakeAnim.getKeyFrames().add(new KeyFrame(time,
				new KeyValue(this.targetPointXProp, target.getX(), Interpolator.DISCRETE),
				new KeyValue(this.targetPointYProp, target.getY(), Interpolator.DISCRETE),
				new KeyValue(foreground.viewportProperty(), new Rectangle2D(x, beforeY, sourceFrameWidth, sourceFrameHeight), Interpolator.DISCRETE)
			));
			afterShakeAnim.getKeyFrames().add(new KeyFrame(time,
				new KeyValue(foreground.viewportProperty(), new Rectangle2D(x, afterY, sourceFrameWidth, sourceFrameHeight), Interpolator.DISCRETE)
			));
		}
		afterShakeAnim.getKeyFrames().add(new KeyFrame(frameLength.multiply(framesPerRow),
			new KeyValue(foreground.viewportProperty(), hiddenViewport)
		));
		
		return new SequentialTransition(
			panAnimation,
			beforeShakeAnim,
			new ParallelTransition(
				new ParallelTransition(
					shakeAnimation.apply(),
					hitAnimation
				),
				afterShakeAnim
			)
		);
	}
}
