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
package name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
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

public final class InfantryLancer implements UnitAnimationGroup {
	private final Duration lanceLift = Duration.millis(120);
	private final Duration windup = Duration.millis(600);
	private final Duration thrust = Duration.millis(900);
	
	private final Duration extract = Duration.millis(300);
	
	private final WritableLancerControlPoints controlPoints;
	
	private final Group frontLayer;
	private final Group backLayer;
	
	public InfantryLancer() {
		this.controlPoints = new WritableLancerControlPoints();
		
		this.backLayer = this.controlPoints.createFigure();
		this.frontLayer = new Group();
	}
	
	@Override public Node objectBehindLayer() { return this.backLayer; }
	//@Override public Node objectFrontLayer() { return this.frontLayer; }
	
	@Override public Point2D getSpellTarget(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return new Point2D(
			(
				  rolloverKeyValues.get(controlPoints.leftPelvic.writableX)
				+ rolloverKeyValues.get(controlPoints.rightPelvic.writableX)
				+ rolloverKeyValues.get(controlPoints.leftShoulder.writableX)
				+ rolloverKeyValues.get(controlPoints.rightShoulder.writableX)
			) / 4,
			(
				  rolloverKeyValues.get(controlPoints.leftPelvic.writableY)
				+ rolloverKeyValues.get(controlPoints.rightPelvic.writableY)
				+ rolloverKeyValues.get(controlPoints.leftShoulder.writableY)
				+ rolloverKeyValues.get(controlPoints.rightShoulder.writableY)
			) / 4
		);
	}
	
	@Override public double getCurrentXOffset(Map<WritableDoubleValue, Double> rolloverKeyValues) {
		return rolloverKeyValues.get(controlPoints.head.writableX);
	}
	
	@Override public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Map<WritableDoubleValue, Double> rolloverKeyValues
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> attackerModifiers
		, boolean isFinisher
	) {
		final LancerTimelineBuilder beforeSpellAnimationBuilder = new LancerTimelineBuilder(controlPoints, rolloverKeyValues);
		beforeSpellAnimationBuilder.appendAddLanceCenter(new Point2D(0, -10), lanceLift, Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.appendAddRightHand(new Point2D(0, -10), lanceLift, Interpolator.EASE_BOTH);
		
		beforeSpellAnimationBuilder.appendAddLeftKnee(new Point2D(0, 0), windup.divide(2), Interpolator.LINEAR);
		
		
		beforeSpellAnimationBuilder.appendAddLeftFoot(new Point2D(0, 0), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftKnee(new Point2D(-10, 0), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftPelvic(new Point2D(-20, 5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightFoot(new Point2D(-30, 0), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightKnee(new Point2D(-20, 3.5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightPelvic(new Point2D(-20, 6.5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightShoulder(new Point2D(-20, 6.5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightElbow(new Point2D(-20, 6.5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightHand(new Point2D(-30, 25), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLanceCenter(new Point2D(-30, 25), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftShoulder(new Point2D(-20, 5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftElbow(new Point2D(-10, 5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftHand(beforeSpellAnimationBuilder.currentValues().rightHand.add(30, 0), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddHead(new Point2D(-20, 5), windup, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLanceControl(beforeSpellAnimationBuilder.currentValues().lanceCenter.add(-60, 0), windup, Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.appendAddLeftFoot(new Point2D(0, 0), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftKnee(new Point2D(20, 0), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftPelvic(new Point2D(20, 1.5), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightFoot(new Point2D(10, 0), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightKnee(new Point2D(15, 0), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightPelvic(new Point2D(20, -1.5), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightShoulder(new Point2D(20, -1.5), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightElbow(new Point2D(40, 2.5), thrust, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.appendAddRightHand(new Point2D(80, 0), thrust, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.appendAddLanceCenter(new Point2D(100, 0), thrust, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.appendAddLeftShoulder(new Point2D(20, 1.5), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftElbow(new Point2D(20, 0), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftHand(new Point2D(80, 0), thrust, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.appendAddHead(new Point2D(20, 1.5), thrust, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLanceControl(new Point2D(-40, 0), thrust, Interpolator.LINEAR);
		
		final LancerControlPoints midValues = beforeSpellAnimationBuilder.currentValues();
		final LancerTimelineBuilder afterSpellAnimationBuilder = new LancerTimelineBuilder(controlPoints, midValues);
		
		afterSpellAnimationBuilder.appendAddLeftFoot(new Point2D(0, 0), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftKnee(new Point2D(-20, 0), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftPelvic(new Point2D(-20, -1.5), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightFoot(new Point2D(0, 0), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightKnee(new Point2D(-15, 0), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightPelvic(new Point2D(-20, 1.5), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightShoulder(new Point2D(-20, 1.5), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightElbow(new Point2D(-40, -2.5), extract, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.appendAddRightHand(new Point2D(-80, 0), extract, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.appendAddLanceCenter(new Point2D(-100, 0), extract, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.appendAddLeftShoulder(new Point2D(-20, -1.5), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftElbow(new Point2D(-20, 0), extract, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftHand(new Point2D(-80, 0), extract, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.appendAddHead(new Point2D(-20, -1.5), extract, Interpolator.LINEAR);
		
		afterSpellAnimationBuilder.appendAddLeftFoot(new Point2D(0, 0), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftKnee(new Point2D(10, 0), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftPelvic(new Point2D(20, -5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightFoot(new Point2D(20, 0), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightKnee(new Point2D(20, -3.5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightPelvic(new Point2D(20, -6.5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightShoulder(new Point2D(20, -6.5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightElbow(new Point2D(20, -6.5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddRightHand(new Point2D(30, -15), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLanceCenter(new Point2D(30, -15), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftShoulder(new Point2D(20, -5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftElbow(new Point2D(10, -5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftHand(afterSpellAnimationBuilder.currentValues().leftElbow.add(10, 5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddHead(new Point2D(20, -5), windup, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLanceControl(afterSpellAnimationBuilder.currentValues().lanceCenter.add(0, 60), windup, Interpolator.LINEAR);
		
		
		afterSpellAnimationBuilder.storeInMap(rolloverKeyValues);
		return new SequentialTransition(
			beforeSpellAnimationBuilder.timeline(),
			spellAnimationFun.apply(midValues.lanceTip()),
			afterSpellAnimationBuilder.timeline()
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
		
		final LancerTimelineBuilder vals = new LancerTimelineBuilder(controlPoints, new LancerControlPoints(Point2D.ZERO));
		
		vals.appendSetLeftFoot(footPoint.add(6, -2), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLeftKneeRelativeToLeftFoot(new Point2D(0, -15), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLeftPelvicRelativeToLeftKnee(new Point2D(0, -15), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLeftElbowRelativeToLeftShoulder(new Point2D(10, -2), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLeftHandRelativeToLeftElbow(new Point2D(10, -2), Duration.ZERO, Interpolator.LINEAR);
		
		vals.appendSetRightFoot(footPoint.add(-6, 2), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetRightKneeRelativeToRightFoot(new Point2D(0, -15), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetRightPelvicRelativeToRightKnee(new Point2D(0, -15), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetRightElbowRelativeToRightShoulder(new Point2D(-10, 2), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetRightHandRelativeToRightElbow(new Point2D(-10, 7), Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Duration.ZERO, Interpolator.LINEAR);
		vals.appendSetLanceControlRelativeToLanceCenter(new Point2D(0, 60), Duration.ZERO, Interpolator.LINEAR);
		
		vals.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Duration.ZERO, Interpolator.LINEAR);
		vals.storeInMap(retval);
		
		return retval;
	}
}
