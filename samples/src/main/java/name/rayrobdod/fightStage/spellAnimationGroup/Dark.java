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
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A spell animation whose animation inverts the colors around the target
 * <p>
 * Demonstrates a simple use of javafx.scene.effect.BlendMode.EXCLUSION
 */
public final class Dark implements SpellAnimationGroup {
	
	private static final Duration fadeInTime = Duration.seconds(0.3);
	private static final Duration fadeOutTime = Duration.seconds(0.2);
	private static final Duration stayTime = Duration.seconds(0.5);
	private static final Duration endDelayTime = Duration.seconds(0.1);
	private static final Color mainColor = Color.rgb(224, 196, 255);
	private static final int mainRadius = 80;
	private static final double defaultCenterX = 200;
	private static final double defaultCenterY = 200;
	
	
	private final Circle node;
	private final Circle backLayer;
	
	public Dark() {
		this.node = new Circle();
		this.node.setFill(Color.BLACK);
		this.node.setBlendMode(BlendMode.EXCLUSION);
		this.backLayer = new Circle();
	}
	
	public Node objectBehindLayer() { return this.backLayer; }
	public Node objectFrontLayer() { return this.node; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(this.node.centerXProperty(), target.getX(), Interpolator.DISCRETE),
			new KeyValue(this.node.centerYProperty(), target.getY(), Interpolator.DISCRETE),
			new KeyValue(this.node.fillProperty(), Color.BLACK, Interpolator.DISCRETE),
			new KeyValue(this.node.radiusProperty(), mainRadius, Interpolator.DISCRETE)
		));
		// Timeline apparently will not touch something without it being mentioned at least twice
		timeline.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(this.node.centerXProperty(), target.getX(), Interpolator.DISCRETE),
			new KeyValue(this.node.centerYProperty(), target.getY(), Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeInTime,
			new KeyValue(this.node.fillProperty(), mainColor, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeInTime.add(stayTime),
			new KeyValue(this.node.radiusProperty(), mainRadius, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeInTime.add(stayTime).add(fadeOutTime),
			new KeyValue(this.node.radiusProperty(), 0, Interpolator.LINEAR)
		));
		
		return new SequentialTransition(
			panAnimation.panToDefender(),
			timeline,
			new ParallelTransition(
				shakeAnimation.apply(),
				hitAnimation,
				new PauseTransition(endDelayTime)
			)
		);
	}
	
}
