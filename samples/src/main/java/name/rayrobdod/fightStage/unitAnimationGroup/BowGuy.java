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
package name.rayrobdod.fightStage.unitAnimationGroup;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.UnitAnimationGroup;

public final class BowGuy implements UnitAnimationGroup {
	
	private static final String filename = "/name/rayrobdod/fightStage/unitAnimationGroup/bowguy.png";
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,100,130);
	private static final Duration frameLength = Duration.seconds(1.0 / 8.0);
	private static final Rectangle2D[] beforeSpellViewports = {
		standingViewport,
		new Rectangle2D(100,0,100,130),
		new Rectangle2D(200,0,100,130),
		new Rectangle2D(300,0,100,130),
		new Rectangle2D(400,0,100,130)
	};
	private static final Rectangle2D[] beforeConsecutiveSpellViewports = {
		new Rectangle2D(400,0,100,130),
		new Rectangle2D(600,0,100,130),
		new Rectangle2D(200,0,100,130),
		new Rectangle2D(300,0,100,130),
		new Rectangle2D(400,0,100,130)
	};
	private static final Rectangle2D[] afterSpellViewports = {
		new Rectangle2D(400,0,100,130),
		new Rectangle2D(500,0,100,130),
		standingViewport
	};
	
	private final ImageView backLayer;
	private final DoubleProperty scaleXProp;
	private final DoubleProperty translateXProp;
	private final DoubleProperty translateYProp;
	private final DoubleProperty deathRotateProp;
	
	public BowGuy() {
		final Translate footPointTranslate = new Translate(-70, -130);
		final Scale scale = new Scale();
		this.scaleXProp = scale.xProperty();
		final Translate moveTranslate = new Translate();
		this.translateXProp = moveTranslate.xProperty();
		this.translateYProp = moveTranslate.yProperty();
		final Rotate deathRotate = new Rotate();
		this.deathRotateProp = deathRotate.angleProperty();
		deathRotate.setPivotX(20);
		deathRotate.setPivotY(0);
		final Image img = new Image(filename);
		this.backLayer = new ImageView(img);
		this.backLayer.getTransforms().add(moveTranslate);
		this.backLayer.getTransforms().add(scale);
		this.backLayer.getTransforms().add(deathRotate);
		this.backLayer.getTransforms().add(footPointTranslate);
		this.backLayer.setViewport(standingViewport);
	}
	
	@Override
	public Node objectBehindLayer() { return this.backLayer; }
	
	@Override
	public Point2D getSpellTarget(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(translateXProp) - rolloverKeyValues.get(scaleXProp) * 5,
			rolloverKeyValues.get(translateYProp) - 60
		);
	}
	
	@Override
	public double getCurrentXOffset(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return rolloverKeyValues.get(translateXProp);
	}

	private Point2D getSpellOrigin(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(translateXProp) - rolloverKeyValues.get(scaleXProp) * 65,
			rolloverKeyValues.get(translateYProp) - 60
		);
	}
	
	@Override
	public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Map<WritableDoubleValue, Double> rolloverKeyValues
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> attackerModifiers
		, boolean isFinisher
	) {
		final Timeline beforeSpellAnimation = new Timeline();
		if (consecutiveAttackDesc.isFirst()) {
			for (int i = 0; i < beforeSpellViewports.length; i++) {
				final Duration thisTime = frameLength.multiply(i);
				beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
					new KeyValue(backLayer.viewportProperty(), beforeSpellViewports[i], Interpolator.DISCRETE)
				));
			}
		} else {
			for (int i = 0; i < beforeConsecutiveSpellViewports.length; i++) {
				final Duration thisTime = frameLength.multiply(i);
				beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
					new KeyValue(backLayer.viewportProperty(), beforeConsecutiveSpellViewports[i], Interpolator.DISCRETE)
				));
			}
		}
		
		final Timeline afterSpellAnimation = new Timeline();
		if (consecutiveAttackDesc.isLast()) {
			for (int i = 0; i < afterSpellViewports.length; i++) {
				final Duration thisTime = frameLength.multiply(i);
				afterSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
					new KeyValue(backLayer.viewportProperty(), afterSpellViewports[i], Interpolator.DISCRETE)
				));
			}
		}
		
		return new SequentialTransition(
			beforeSpellAnimation,
			spellAnimationFun.apply(this.getSpellOrigin(rolloverKeyValues)),
			afterSpellAnimation
		);
	}
	
	@Override
	public Animation getHitAnimation(
		  Map<WritableDoubleValue, Double> rolloverKeyValues
		, Set<AttackModifier> attackerModifiers
		, Set<AttackModifier> defenderModifiers
		, boolean isFinisher
	) {
		if (isFinisher) {
			return Animations.simpleAnimation(
				Duration.millis(250),
				deathRotateProp,
				0,
				90
			);
		} else {
			return Animations.nil();
		}
	}
	
	@Override public Map<WritableDoubleValue, Double> getInitializingKeyValues(
		  Side side
		, Point2D footPoint
	) {
		final Map<WritableDoubleValue, Double> retval = new java.util.HashMap<>();
		retval.put(scaleXProp, (side == Side.LEFT ? -1.0 : 1.0));
		retval.put(translateXProp, footPoint.getX());
		retval.put(translateYProp, footPoint.getY());
		return retval;
	}
}
