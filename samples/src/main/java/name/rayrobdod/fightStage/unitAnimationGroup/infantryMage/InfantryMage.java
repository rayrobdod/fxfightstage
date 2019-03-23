/*
 * Copyright 2019 Raymond Dodge
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
package name.rayrobdod.fightStage.unitAnimationGroup.infantryMage;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;

import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.unitAnimationGroup.util.*;

public final class InfantryMage implements UnitAnimationGroup {
	private final WritableBoneControls writables;
	private final Group frontLayer;
	private final Group backLayer;

	public InfantryMage() {
		this.writables = new WritableBoneControls();
		this.backLayer = new Skin(this.writables).build();
		this.frontLayer = new Group();
	}

	@Override public Node objectBehindLayer() { return this.backLayer; }
	//@Override public Node objectFrontLayer() { return this.frontLayer; }

	@Override public Point2D getSpellTarget(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return writables.extractFromMap(rolloverKeyValues).spellTarget();
	}

	@Override public double getCurrentXOffset(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return writables.extractFromMap(rolloverKeyValues).xOffset();
	}

	@Override public Animation getAttackAnimation(
		Function<Point2D, Animation> spellAnimationFun
		, Map<WritableDoubleValue, Double> rolloverKeyValues
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> attackerModifiers
		, boolean isFinisher
	) {
		final double side = Math.signum(target.getX() - getCurrentXOffset(rolloverKeyValues));
		final double leftPerspective = -2 * side;
		final double rightPerspective = 2 * side;

		final TimelineBuilder beforeTimeline = new TimelineBuilder(writables, writables.extractFromMap(rolloverKeyValues));
		beforeTimeline.append(Duration.ONE, BoneControlsOptional.NIL
				.copyWithSomePivot(new Pivot(PivotType.LeftFoot, beforeTimeline.currentValues().leftFoot()))
		);
		beforeTimeline.append(Duration.seconds(0.5), BoneControlsOptional.NIL
				.copyWithSomeLeftElbowToLeftHandAngle(Math.PI / 2)
				.copyWithSomeLeftShoulderToLeftElbowAngle(Math.PI / 2)
		);
		beforeTimeline.append(Duration.seconds(0.5), BoneControlsOptional.NIL
				.copyWithSomeLeftElbowToLeftHandAngle(Math.PI)
		);

		final BoneControls midValues = beforeTimeline.currentValues();
		final TimelineBuilder afterTimeline = new TimelineBuilder(writables, midValues);
		
		writables.storeInMap(rolloverKeyValues, beforeTimeline.currentValues());
		
		return new SequentialTransition(
			  beforeTimeline.build()
			, spellAnimationFun.apply(Point2D.ZERO)
			, afterTimeline.build()
		);
	}

	@Override public Animation getInitiateAnimation() {
		final Timeline anim = new Timeline();
		return anim;
	}

	@Override public Animation getVictoryAnimation() {
		final Timeline anim = new Timeline();
		return anim;
	}

	@Override public Map<WritableDoubleValue, Double> getInitializingKeyValues(
		  Side side
		, Point2D footPoint
	) {
		final Map<WritableDoubleValue, Double> retval = new java.util.HashMap<>();
		final double leftPerspective = (side == Side.LEFT ? -2 : 2);
		final double rightPerspective = (side == Side.LEFT ? 2 : -2);

		final BoneControls values = BoneControls.ZERO
				.copyWithPivot(new Pivot(PivotType.LeftFoot, footPoint.add(-6, leftPerspective)))
				.copyWithLeftFootToLeftKnee(15.0, -Math.PI / 2)
				.copyWithLeftKneeToLeftPelvic(15.0, -Math.PI / 2)
				.copyWithLeftPelvicToRightPelvic(Math.sqrt(12 * 12 + 4 * 4), Math.atan2(rightPerspective * 2, -12))
				.copyWithRightKneeToRightFoot(15.0, Math.PI / 2)
				.copyWithRightPelvicToRightKnee(15.0, Math.PI / 2)
				.copyWithCenterPelvicToNeck(35.0, -Math.PI / 2)
				.copyWithNeckToLeftShoulder(Math.sqrt(104), Math.atan2(leftPerspective * 10/6, 10))
				.copyWithNeckToRightShoulder(Math.sqrt(104), Math.atan2(rightPerspective * 10/6, -10))
				.copyWithLeftShoulderToLeftElbow(15.0, 0)
				.copyWithLeftElbowToLeftHand(15.0, 0)
				.copyWithRightShoulderToRightElbow(15.0, Math.PI)
				.copyWithRightElbowToRightHand(15.0, Math.PI)
				;

		writables.storeInMap(retval, values);

		return retval;
	}
}
