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
		beforeSpellAnimationBuilder.addRightHand(new Point2D(0, -10), Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.addRightElbow(new Point2D(0, -5), Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.nextFrame(lanceLift.divide(2));
		beforeSpellAnimationBuilder.addLanceTipDistance(20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.addLanceButtDistance(-20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.addRightHand(Point2D.ZERO, Interpolator.EASE_BOTH);
		beforeSpellAnimationBuilder.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.nextFrame(windup.divide(2).subtract(lanceLift));
		beforeSpellAnimationBuilder.addLeftFoot(new Point2D(0, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.addLeftKnee(new Point2D(-5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftPelvicRelativeToLeftKnee(new Point2D(-15 * Math.sqrt(2), -15 / 2), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightPelvicRelativeToLeftPelvic(new Point2D(-12, rightPerspective - leftPerspective), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.addRightFoot(new Point2D(-30, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightKneeRelativeToRightLeg(Point2D::midpoint, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.nextFrame(windup.divide(2));
		beforeSpellAnimationBuilder.addRightFoot(new Point2D(0, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightKneeRelativeToRightFoot(new Point2D(-2, -13), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightPelvicRelativeToRightKnee(new Point2D(2, -13), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftPelvicRelativeToRightPelvic(new Point2D(12, leftPerspective - rightPerspective), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.addLeftFoot(new Point2D(-5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftKneeRelativeToLeftLeg(Point2D::midpoint, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightElbowRelativeToRightShoulder(new Point2D(-3, 10), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightHandRelativeToRightElbow(new Point2D(3, 10), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLanceAngleToPointAt(target, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.setLeftHandRelativeToLance(20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftElbowRelativeToLeftArm((a, b) -> a.midpoint(b).add(6, -3), Interpolator.LINEAR);
		
		beforeSpellAnimationBuilder.nextFrame(thrust.divide(2));
		beforeSpellAnimationBuilder.addLeftFoot(new Point2D(5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.nextFrame(thrust.divide(2));
		beforeSpellAnimationBuilder.addLeftFoot(new Point2D(0, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftKneeRelativeToLeftFoot(new Point2D(4, -12), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftPelvicRelativeToLeftKnee(new Point2D(-4, -12), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightPelvicRelativeToLeftPelvic(new Point2D(-12, rightPerspective - leftPerspective), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.addRightFoot(new Point2D(5, 0), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightKneeRelativeToRightLeg(Point2D::midpoint, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightHandRelativeToLance(50, Interpolator.EASE_IN);
		beforeSpellAnimationBuilder.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftHandRelativeToLance(20, Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setRightElbowRelativeToRightArm((a, b) -> a.midpoint(b), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setLeftElbowRelativeToLeftArm((a, b) -> a.midpoint(b).add(6, -3), Interpolator.LINEAR);
		beforeSpellAnimationBuilder.setHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		
		final LancerControlPoints midValues = beforeSpellAnimationBuilder.currentValues();
		final LancerTimelineBuilder afterSpellAnimationBuilder = new LancerTimelineBuilder(controlPoints, midValues);
		
		afterSpellAnimationBuilder.nextFrame(extract);
		afterSpellAnimationBuilder.addRightFoot(new Point2D(0, 0), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightKneeRelativeToRightFoot(new Point2D(-2, -13), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightPelvicRelativeToRightKnee(new Point2D(2, -13), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftPelvicRelativeToRightPelvic(new Point2D(12, leftPerspective - rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.addLeftFoot(new Point2D(-5, 0), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftKneeRelativeToLeftLeg(Point2D::midpoint, Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLanceAngleToPointAt(target, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.setRightHandRelativeToLance(-60, Interpolator.EASE_IN);
		afterSpellAnimationBuilder.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftHandRelativeToLance(20, Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightElbowRelativeToRightArm((a, b) -> a.midpoint(b), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftElbowRelativeToLeftArm((a, b) -> a.midpoint(b).add(6, -3), Interpolator.LINEAR);
		
		afterSpellAnimationBuilder.nextFrame(windup.divide(2));
		afterSpellAnimationBuilder.addRightFoot(new Point2D(0, 0), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightKneeRelativeToRightFoot(new Point2D(0, -15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightPelvicRelativeToRightKnee(new Point2D(0, -15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftPelvicRelativeToRightPelvic(new Point2D(12, leftPerspective - rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftKneeRelativeToLeftPelvic(new Point2D(0, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftFootRelativeToLeftKnee(new Point2D(0, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftElbowRelativeToLeftShoulder(new Point2D(2, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLeftHandRelativeToLeftElbow(new Point2D(0, 15), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightElbowRelativeToRightShoulder(new Point2D(-10, 5 + rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setRightHandRelativeToRightElbow(new Point2D(-10, -4 + rightPerspective), Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLanceTipDistance(60, Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLanceButtDistance(60, Interpolator.LINEAR);
		afterSpellAnimationBuilder.setLanceAngle(3 * Math.PI / 2, Interpolator.LINEAR);
		
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
		
		vals.setLeftFoot(footPoint.add(6, leftPerspective), Interpolator.LINEAR);
		vals.setLeftKneeRelativeToLeftFoot(new Point2D(0, -15), Interpolator.LINEAR);
		vals.setLeftPelvicRelativeToLeftKnee(new Point2D(0, -15), Interpolator.LINEAR);
		vals.setLeftShoulderRelativeToLeftPelvic(new Point2D(4, -35), Interpolator.LINEAR);
		vals.setLeftElbowRelativeToLeftShoulder(new Point2D(2, 15), Interpolator.LINEAR);
		vals.setLeftHandRelativeToLeftElbow(new Point2D(0, 15), Interpolator.LINEAR);
		
		vals.setRightFoot(footPoint.add(-6, rightPerspective), Interpolator.LINEAR);
		vals.setRightKneeRelativeToRightFoot(new Point2D(0, -15), Interpolator.LINEAR);
		vals.setRightPelvicRelativeToRightKnee(new Point2D(0, -15), Interpolator.LINEAR);
		vals.setRightShoulderRelativeToRightPelvic(new Point2D(-4, -35), Interpolator.LINEAR);
		vals.setRightElbowRelativeToRightShoulder(new Point2D(-10, 5 + rightPerspective), Interpolator.LINEAR);
		vals.setRightHandRelativeToRightElbow(new Point2D(-10, -4 + rightPerspective), Interpolator.LINEAR);
		vals.setLanceCenterRelativeToRightHand(Point2D.ZERO, Interpolator.LINEAR);
		vals.setLanceAngle(3 * Math.PI / 2, Interpolator.LINEAR);
		vals.setLanceTipDistance(60, Interpolator.LINEAR);
		vals.setLanceButtDistance(60, Interpolator.LINEAR);
		
		vals.setHeadRelativeToShoulders((a, b) -> a.midpoint(b).add(0, -20), Interpolator.LINEAR);
		vals.storeInMap(retval);
		
		return retval;
	}
}
