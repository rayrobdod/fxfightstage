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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A very basic animation mostly intended to debug origin and target points
 */
public final class Lazor implements SpellAnimationGroup {
	
	private static final Duration duration = Duration.seconds(0.5);
	private static final Duration followerDelay = Duration.seconds(1);
	private static final int crossSectionWidth = 20;
	
	private final Path node;
	private final Node background;
	
	private final MoveTo followerLeft;
	private final LineTo followerRight;
	private final LineTo leaderLeft;
	private final LineTo leaderRight;
	
	public Lazor(Color color) {
		this.followerLeft = new MoveTo();
		this.followerRight = new LineTo();
		this.leaderLeft = new LineTo();
		this.leaderRight = new LineTo();
		this.node = new Path(followerLeft, followerRight, leaderRight, leaderLeft);
		this.node.setFill(color);
		this.node.setStroke(Color.TRANSPARENT);
		this.background = new Circle();
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.node; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Point2D deltaDirection = origin.subtract(target).normalize();
		final Point2D crossSectionVector = new Point2D(-deltaDirection.getY(), deltaDirection.getX()).multiply(crossSectionWidth / 2);
		
		
		final Timeline spellAnimation = new Timeline();
		spellAnimation.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(followerLeft.yProperty(), origin.getY() + crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(followerLeft.xProperty(), origin.getX() + crossSectionVector.getX(), Interpolator.LINEAR),
			new KeyValue(followerRight.yProperty(), origin.getY() - crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(followerRight.xProperty(), origin.getX() - crossSectionVector.getX(), Interpolator.LINEAR),
			new KeyValue(leaderLeft.yProperty(), origin.getY() + crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(leaderLeft.xProperty(), origin.getX() + crossSectionVector.getX(), Interpolator.LINEAR),
			new KeyValue(leaderRight.yProperty(), origin.getY() - crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(leaderRight.xProperty(), origin.getX() - crossSectionVector.getX(), Interpolator.LINEAR)
		));
		spellAnimation.getKeyFrames().add(new KeyFrame(followerDelay,
			new KeyValue(followerLeft.yProperty(), origin.getY() + crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(followerLeft.xProperty(), origin.getX() + crossSectionVector.getX(), Interpolator.LINEAR),
			new KeyValue(followerRight.yProperty(), origin.getY() - crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(followerRight.xProperty(), origin.getX() - crossSectionVector.getX(), Interpolator.LINEAR)
		));
		spellAnimation.getKeyFrames().add(new KeyFrame(duration,
			new KeyValue(leaderLeft.yProperty(), target.getY() + crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(leaderLeft.xProperty(), target.getX() + crossSectionVector.getX(), Interpolator.LINEAR),
			new KeyValue(leaderRight.yProperty(), target.getY() - crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(leaderRight.xProperty(), target.getX() - crossSectionVector.getX(), Interpolator.LINEAR)
		));
		spellAnimation.getKeyFrames().add(new KeyFrame(duration.add(followerDelay),
			new KeyValue(followerLeft.yProperty(), target.getY() + crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(followerLeft.xProperty(), target.getX() + crossSectionVector.getX(), Interpolator.LINEAR),
			new KeyValue(followerRight.yProperty(), target.getY() - crossSectionVector.getY(), Interpolator.LINEAR),
			new KeyValue(followerRight.xProperty(), target.getX() - crossSectionVector.getX(), Interpolator.LINEAR)
		));
		
		return new ParallelTransition(
			panAnimation.panToDefender(),
			spellAnimation,
			new SequentialTransition(
				new PauseTransition(duration),
				new ParallelTransition(
					shakeAnimation.apply(4, followerDelay),
					hitAnimation
				)
			)
		);
	}
	
}
