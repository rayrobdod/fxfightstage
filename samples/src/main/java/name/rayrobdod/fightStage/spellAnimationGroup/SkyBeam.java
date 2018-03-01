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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * Kinda basic target-only, foreground-only animation
 */
public final class SkyBeam implements SpellAnimationGroup {
	
	private static final double beamHeight = 500;
	private static final double fadeInMaxWidth = 400;
	private static final double fadeInMinWidth = 3.5;
	private static final double attackWidth = 60;
	private static final double footPointDeltaY = 60;
	// private static final Color beamColor = Color.MEDIUMPURPLE;
	// private static final Color beamColor = Color.GOLDENROD;
	private static final Color beamColor = Color.rgb(0xDD, 0xCC, 0x66);
	
	private static final Duration prepareDur = Duration.seconds(0.8);
	private static final Duration pauseDur = Duration.seconds(0.2);
	private static final Duration attackInDur = Duration.seconds(0.05);
	private static final Duration attackStayDur = Duration.seconds(0.35);
	private static final Duration attackOutDur = Duration.seconds(0.3);
	
	private final Node background;
	private final Node foreground;
	private final Rectangle beamShape;
	
	public SkyBeam() {
		this.beamShape = new Rectangle();
		this.beamShape.setFill(beamColor);
		this.beamShape.setHeight(beamHeight);
		this.beamShape.setOpacity(0.0);
		this.beamShape.setBlendMode(javafx.scene.effect.BlendMode.SCREEN);
		
		this.background = new Group(
		);
		
		this.foreground = new Group(
			this.beamShape
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
		final Timeline prepareAnimation = new Timeline();
		prepareAnimation.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(beamShape.yProperty(), target.getY() - beamHeight + footPointDeltaY, Interpolator.LINEAR),
			new KeyValue(beamShape.widthProperty(), fadeInMaxWidth, Interpolator.LINEAR),
			new KeyValue(beamShape.xProperty(), target.getX() - fadeInMaxWidth / 2, Interpolator.LINEAR),
			new KeyValue(beamShape.opacityProperty(), 0, Interpolator.LINEAR)
		));
		prepareAnimation.getKeyFrames().add(new KeyFrame(prepareDur,
			new KeyValue(beamShape.yProperty(), target.getY() - beamHeight + footPointDeltaY, Interpolator.LINEAR),
			new KeyValue(beamShape.widthProperty(), fadeInMinWidth, Interpolator.EASE_OUT),
			new KeyValue(beamShape.xProperty(), target.getX() - fadeInMinWidth / 2, Interpolator.EASE_OUT),
			new KeyValue(beamShape.opacityProperty(), 1, Interpolator.EASE_OUT)
		));
		
		final Timeline attackAnimation = new Timeline();
		attackAnimation.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(beamShape.widthProperty(), fadeInMinWidth, Interpolator.LINEAR),
			new KeyValue(beamShape.xProperty(), target.getX() - fadeInMinWidth / 2, Interpolator.LINEAR),
			new KeyValue(beamShape.opacityProperty(), 1, Interpolator.LINEAR)
		));
		attackAnimation.getKeyFrames().add(new KeyFrame(attackInDur,
			new KeyValue(beamShape.widthProperty(), attackWidth, Interpolator.EASE_BOTH),
			new KeyValue(beamShape.xProperty(), target.getX() - attackWidth / 2, Interpolator.EASE_BOTH)
		));
		attackAnimation.getKeyFrames().add(new KeyFrame(attackInDur.add(attackStayDur),
			new KeyValue(beamShape.widthProperty(), attackWidth, Interpolator.LINEAR),
			new KeyValue(beamShape.xProperty(), target.getX() - attackWidth / 2, Interpolator.LINEAR),
			new KeyValue(beamShape.opacityProperty(), 1, Interpolator.LINEAR)
		));
		attackAnimation.getKeyFrames().add(new KeyFrame(attackInDur.add(attackStayDur).add(attackOutDur),
			new KeyValue(beamShape.widthProperty(), attackWidth, Interpolator.LINEAR),
			new KeyValue(beamShape.xProperty(), target.getX() - attackWidth / 2, Interpolator.LINEAR),
			new KeyValue(beamShape.opacityProperty(), 0, Interpolator.EASE_IN)
		));
		
		
		return new SequentialTransition(
			new ParallelTransition(
				panAnimation,
				new SequentialTransition(
					prepareAnimation,
					new PauseTransition(pauseDur)
				)
			),
			new ParallelTransition(
				hpAndShakeAnimation,
				attackAnimation
			)
		);
	}
}
