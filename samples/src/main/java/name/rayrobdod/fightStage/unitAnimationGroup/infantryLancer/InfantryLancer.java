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
	private final Duration windup = Duration.millis(600 - 120);
	private final Duration thrust = Duration.millis(300);
	
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
		final double side = Math.signum(target.getX() - getCurrentXOffset(rolloverKeyValues));
		final double leftPerspective = -2 * side;
		final double rightPerspective = 2 * side;
		
		final LancerTimelineBuilder beforeSpellAnimationBuilder = new LancerTimelineBuilder(controlPoints, rolloverKeyValues);
		beforeSpellAnimationBuilder.nextFrame(lanceLift.divide(2));
		beforeSpellAnimationBuilder.appendAddRightHand(new Point2D(0, -10), Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.appendAddRightElbow(new Point2D(0, -5), Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.nextFrame(lanceLift.divide(2));
		beforeSpellAnimationBuilder.appendAddLanceTipDistance(20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLanceButtDistance(-20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightHand(Point2D.ZERO, Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.nextFrame(windup.divide(2).subtract(lanceLift));
		beforeSpellAnimationBuilder.appendAddLeftFoot(new Point2D(0, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftKnee(new Point2D(-5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftPelvicRelativeToLeftKnee(new Point2D(-15 * Math.sqrt(2), -15 / 2), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightPelvicRelativeToLeftPelvic(new Point2D(-12, rightPerspective - leftPerspective), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightFoot(new Point2D(-30, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightKneeRelativeToRightLeg(Point2D::midpoint, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.nextFrame(windup.divide(2));
		beforeSpellAnimationBuilder.appendAddRightFoot(new Point2D(0, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightKneeRelativeToRightFoot(new Point2D(-2, -13), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightPelvicRelativeToRightKnee(new Point2D(2, -13), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftPelvicRelativeToRightPelvic(new Point2D(12, leftPerspective - rightPerspective), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddLeftFoot(new Point2D(-5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftKneeRelativeToLeftLeg(Point2D::midpoint, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightElbowRelativeToRightShoulder(new Point2D(-3, 10), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightHandRelativeToRightElbow(new Point2D(3, 10), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLanceAngleToPointAt(target, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.appendSetLeftHandRelativeToLance(20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftElbowRelativeToLeftArm((a, b) -> a.midpoint(b).add(6, -3), Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.nextFrame(thrust.divide(2));
		beforeSpellAnimationBuilder.appendAddLeftFoot(new Point2D(5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.nextFrame(thrust.divide(2));
		beforeSpellAnimationBuilder.appendAddLeftFoot(new Point2D(0, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftKneeRelativeToLeftFoot(new Point2D(4, -12), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftPelvicRelativeToLeftKnee(new Point2D(-4, -12), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightPelvicRelativeToLeftPelvic(new Point2D(-12, rightPerspective - leftPerspective), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendAddRightFoot(new Point2D(5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightKneeRelativeToRightLeg(Point2D::midpoint, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightHandRelativeToLance(50, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftHandRelativeToLance(20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetRightElbowRelativeToRightArm((a, b) -> a.midpoint(b), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetLeftElbowRelativeToLeftArm((a, b) -> a.midpoint(b).add(6, -3), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		
		final LancerControlPoints midValues = beforeSpellAnimationBuilder.currentValues();
		final LancerTimelineBuilder afterSpellAnimationBuilder = new LancerTimelineBuilder(controlPoints, midValues);
		
		afterSpellAnimationBuilder.nextFrame(extract);
		afterSpellAnimationBuilder.appendAddRightFoot(new Point2D(0, 0), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightKneeRelativeToRightFoot(new Point2D(-2, -13), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightPelvicRelativeToRightKnee(new Point2D(2, -13), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftPelvicRelativeToRightPelvic(new Point2D(12, leftPerspective - rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendAddLeftFoot(new Point2D(-5, 0), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftKneeRelativeToLeftLeg(Point2D::midpoint, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLanceAngleToPointAt(target, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.appendSetRightHandRelativeToLance(-60, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftHandRelativeToLance(20, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightElbowRelativeToRightArm((a, b) -> a.midpoint(b), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftElbowRelativeToLeftArm((a, b) -> a.midpoint(b).add(6, -3), Interpolator.LINEAR);
		
		afterSpellAnimationBuilder.nextFrame(windup.divide(2));
		afterSpellAnimationBuilder.appendAddRightFoot(new Point2D(0, 0), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightKneeRelativeToRightFoot(new Point2D(0, -15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightPelvicRelativeToRightKnee(new Point2D(0, -15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftPelvicRelativeToRightPelvic(new Point2D(12, leftPerspective - rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftKneeRelativeToLeftPelvic(new Point2D(0, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftFootRelativeToLeftKnee(new Point2D(0, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftElbowRelativeToLeftShoulder(new Point2D(2, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLeftHandRelativeToLeftElbow(new Point2D(0, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightElbowRelativeToRightShoulder(new Point2D(-10, 5 + rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetRightHandRelativeToRightElbow(new Point2D(-10, -4 + rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLanceTipDistance(60, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLanceButtDistance(60, Interpolator.LINEAR);
		afterSpellAnimationBuilder.appendSetLanceAngle(3 * Math.PI / 2, Interpolator.LINEAR);
		
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
		
		final double leftPerspective = (side == Side.LEFT ? -2 : 2);
		final double rightPerspective = (side == Side.LEFT ? 2 : -2);
		
		final LancerTimelineBuilder vals = new LancerTimelineBuilder(controlPoints, LancerControlPoints.ZERO);
		
		vals.appendSetLeftFoot(footPoint.add(6, leftPerspective), Interpolator.LINEAR);
		vals.appendSetLeftKneeRelativeToLeftFoot(new Point2D(0, -15), Interpolator.LINEAR);
		vals.appendSetLeftPelvicRelativeToLeftKnee(new Point2D(0, -15), Interpolator.LINEAR);
		vals.appendSetLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		vals.appendSetLeftElbowRelativeToLeftShoulder(new Point2D(2, 15), Interpolator.LINEAR);
		vals.appendSetLeftHandRelativeToLeftElbow(new Point2D(0, 15), Interpolator.LINEAR);
		
		vals.appendSetRightFoot(footPoint.add(-6, rightPerspective), Interpolator.LINEAR);
		vals.appendSetRightKneeRelativeToRightFoot(new Point2D(0, -15), Interpolator.LINEAR);
		vals.appendSetRightPelvicRelativeToRightKnee(new Point2D(0, -15), Interpolator.LINEAR);
		vals.appendSetRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		vals.appendSetRightElbowRelativeToRightShoulder(new Point2D(-10, 5 + rightPerspective), Interpolator.LINEAR);
		vals.appendSetRightHandRelativeToRightElbow(new Point2D(-10, -4 + rightPerspective), Interpolator.LINEAR);
		vals.appendSetLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		vals.appendSetLanceAngle(3 * Math.PI / 2, Interpolator.LINEAR);
		vals.appendSetLanceTipDistance(60, Interpolator.LINEAR);
		vals.appendSetLanceButtDistance(60, Interpolator.LINEAR);
		
		vals.appendSetHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		vals.storeInMap(retval);
		
		return retval;
	}
}
