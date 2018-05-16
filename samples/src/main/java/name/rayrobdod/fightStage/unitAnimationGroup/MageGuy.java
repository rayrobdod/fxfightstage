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
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
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

public final class MageGuy implements UnitAnimationGroup {
	
	private static final String filename = "/name/rayrobdod/fightStage/unitAnimationGroup/mageguy.png";
	
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,120,150);
	
	private static final Rectangle2D[] beforeSpellViewports = {
		standingViewport,
		new Rectangle2D(120,0,120,150),
		new Rectangle2D(240,0,120,150),
		new Rectangle2D(360,0,120,150)
	};
	private static final Rectangle2D[] duringSpellViewports = {
		new Rectangle2D(480,0,120,150),
		new Rectangle2D(360,0,120,150)
	};
	private static final Rectangle2D[] afterSpellViewports = {
		new Rectangle2D(240,0,120,150),
		new Rectangle2D(120,0,120,150),
		standingViewport
	};
	
	private static final Duration frameLength = Duration.seconds(1.0 / 15.0);
	
	private final ImageView node;
	private final DoubleProperty scaleXProp;
	private final DoubleProperty translateXProp;
	private final DoubleProperty translateYProp;
	private final DoubleProperty deathRotateProp;
	
	public MageGuy() {
		final Translate footPointTranslate = new Translate(-80, -150);
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
		this.node = new ImageView(img);
		this.node.getTransforms().add(moveTranslate);
		this.node.getTransforms().add(scale);
		this.node.getTransforms().add(deathRotate);
		this.node.getTransforms().add(footPointTranslate);
		this.node.setViewport(standingViewport);
	}
	
	@Override
	public Node getNode() { return this.node; }
	
	@Override
	public Point2D getSpellTarget(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(translateXProp) - rolloverKeyValues.get(scaleXProp) * 5,
			rolloverKeyValues.get(translateYProp) - 60
		);
	}
	
	@Override
	public double getCurrentXOffset(Map<DoubleProperty, Double> rolloverKeyValues) {
		return rolloverKeyValues.get(translateXProp);
	}
	
	private Point2D getSpellOrigin(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(translateXProp) - rolloverKeyValues.get(scaleXProp) * 55,
			rolloverKeyValues.get(translateYProp) - 61
		);
	}
	
	@Override
	public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Map<DoubleProperty, Double> rolloverKeyValues
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> attackerModifiers
		, boolean isFinisher
	) {
		final Timeline beforeSpellAnimation = new Timeline();
		for (int i = 0; i < beforeSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), beforeSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		final Timeline duringSpellAnimation = new Timeline();
		for (int i = 0; i < duringSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i + 1);
			duringSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), duringSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		final Timeline afterSpellAnimation = new Timeline();
		for (int i = 0; i < afterSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			afterSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), afterSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		final Animation spellAnimation = spellAnimationFun.apply(this.getSpellOrigin(rolloverKeyValues));
		duringSpellAnimation.setCycleCount((int) (
			spellAnimation.getTotalDuration().toMillis() / duringSpellAnimation.getCycleDuration().toMillis()
		));
		
		return new SequentialTransition(
			beforeSpellAnimation,
			new ParallelTransition(
				spellAnimation,
				duringSpellAnimation
			),
			afterSpellAnimation
		);
	}
	
	@Override
	public Animation getHitAnimation(
		  Map<DoubleProperty, Double> rolloverKeyValues
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
	
	@Override public Map<DoubleProperty, Double> getInitializingKeyValues(
		  Side side
		, Point2D footPoint
	) {
		final Map<DoubleProperty, Double> retval = new java.util.HashMap<>();
		retval.put(scaleXProp, (side == Side.LEFT ? -1.0 : 1.0));
		retval.put(translateXProp, footPoint.getX());
		retval.put(translateYProp, footPoint.getY());
		return retval;
	}
}
