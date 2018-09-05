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

import static name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer.Point2dPathElements.newBoundCubicCurveTo;
import static name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer.Point2dPathElements.newBoundLineTo;
import static name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer.Point2dPathElements.newBoundMoveTo;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Path;

/**
 * Partial Classes! :D
 */
interface WritableLancerControlPointsOps {
	WritableLancerControlPoints self();
	
	static final double LANCE_HALFWIDTH = 3;
	static final double LEG_HALFWIDTH = 7;
	
	default Node createStickFigure() {
		final Path retval = new Path(
			  newBoundMoveTo(self().leftFoot)
			, newBoundLineTo(self().leftKnee)
			, newBoundLineTo(self().leftPelvic)
			, newBoundLineTo(self().rightPelvic)
			, newBoundLineTo(self().rightKnee)
			, newBoundLineTo(self().rightFoot)
			, newBoundMoveTo(self().leftHand)
			, newBoundLineTo(self().leftElbow)
			, newBoundLineTo(self().leftShoulder)
			, newBoundLineTo(self().rightShoulder)
			, newBoundLineTo(self().rightElbow)
			, newBoundLineTo(self().rightHand)
			, newBoundMoveTo(self().rightPelvic)
			, newBoundLineTo(self().rightShoulder)
			, newBoundLineTo(self().head)
			, newBoundLineTo(self().leftShoulder)
			, newBoundLineTo(self().leftPelvic)
		);
		retval.setStroke(Color.BLACK);
		retval.setFill(Color.TRANSPARENT);
		retval.setStrokeWidth(2);
		return retval;
	}
	
	default Node createLance() {
		final Point2dExpression center = self().lanceCenter;
		final Point2dExpression direction = center.subtract(self().lanceControl).normalize();
		final Point2dExpression perp = direction.perpendicular();
		
		
		final Path shaft = new Path(
			  newBoundMoveTo(center.add(direction.multiply(60)).add(perp.multiply(LANCE_HALFWIDTH)))
			, newBoundLineTo(center.add(direction.multiply(60)).add(perp.multiply(-LANCE_HALFWIDTH)))
			, newBoundLineTo(center.add(direction.multiply(-60)).add(perp.multiply(-LANCE_HALFWIDTH)))
			, newBoundCubicCurveTo(
				  center.add(direction.multiply(-65)).add(perp.multiply(-LANCE_HALFWIDTH))
				, center.add(direction.multiply(-65)).add(perp.multiply(LANCE_HALFWIDTH))
				, center.add(direction.multiply(-60)).add(perp.multiply(LANCE_HALFWIDTH))
			  )
			, newBoundLineTo(center.add(direction.multiply(60)).add(perp.multiply(LANCE_HALFWIDTH)))
		);
		shaft.setStroke(Color.BLACK);
		shaft.setFill(Color.BROWN);
		shaft.setStrokeWidth(1);
		
		final Path head = new Path(
			  newBoundMoveTo(center.add(direction.multiply(55)).add(perp.multiply(7.5)))
			, newBoundLineTo(center.add(direction.multiply(55)).add(perp.multiply(-7.5)))
			, newBoundLineTo(center.add(direction.multiply(70)).add(perp.multiply(0)))
			, newBoundLineTo(center.add(direction.multiply(55)).add(perp.multiply(7.5)))
		);
		head.setStroke(Color.BLACK);
		head.setFill(Color.SILVER);
		head.setStrokeWidth(1);
		
		return new Group(shaft, head);
	}
	
	default Node createPantSeat() {
		final Point2dExpression left = self().leftPelvic;
		final Point2dExpression right = self().rightPelvic;
		
		final Path leg = new Path(
			// front
			newBoundMoveTo(left.add(new Point2D(LEG_HALFWIDTH, 2))),
			newBoundLineTo(left.add(new Point2D(LEG_HALFWIDTH, -10))),
			newBoundLineTo(right.add(new Point2D(-LEG_HALFWIDTH, -10))),
			newBoundLineTo(right.add(new Point2D(-LEG_HALFWIDTH, 2))),
			new ClosePath()
		);
		leg.setStroke(Color.BLACK);
		leg.setFill(Color.BLUE);
		leg.setStrokeWidth(1);
		return leg;
	}
	
	default Node createLeftLeg() {
		return createLeg(self().leftKnee, self().leftFoot, self().leftPelvic);
	}
	
	default Node createRightLeg() {
		return createLeg(self().rightKnee, self().rightFoot, self().rightPelvic);
	}
	
	public static Node createLeg(final Point2dExpression pivot, final Point2dExpression edge1, final Point2dExpression edge2) {
		final Point2dExpression dir1 = edge1.subtract(pivot).normalize();
		final Point2dExpression perp1 = dir1.perpendicular();
		final Point2dExpression dir2 = pivot.subtract(edge2).normalize();
		final Point2dExpression perp2 = dir2.perpendicular();
		
		final Point2dExpression backOfFoot = edge1.add(perp1.multiply(LEG_HALFWIDTH)).interception(dir1, new Point2D(0,0), new Point2D(1, 0));
		final Point2dExpression backOfPelvic = edge2.add(perp2.multiply(LEG_HALFWIDTH));
		final Point2dExpression frontOfFoot = edge1.add(perp1.multiply(-LEG_HALFWIDTH)).interception(dir1, new Point2D(0,0), new Point2D(1, 0));
		final Point2dExpression frontOfPelvic = edge2.add(perp2.multiply(-LEG_HALFWIDTH));
		final Point2dExpression frontOfKnee2 = pivot.add(perp2.multiply(-LEG_HALFWIDTH));
		final Point2dExpression frontOfKnee1 = pivot.add(perp1.multiply(-LEG_HALFWIDTH));
		
		final Path leg = new Path(
			// front
			newBoundMoveTo(frontOfPelvic),
			newBoundLineTo(frontOfKnee2),
			newBoundCubicCurveTo(
				frontOfKnee2.add(dir2.multiply(frontOfKnee2.subtract(frontOfKnee1).magnitude().multiply(0.75))),
				frontOfKnee1.add(dir1.multiply(frontOfKnee1.subtract(frontOfKnee2).magnitude().multiply(-0.75))),
				frontOfKnee1
			),
			newBoundLineTo(frontOfFoot),
			// back
			newBoundLineTo(backOfFoot),
			newBoundLineTo(backOfFoot.interception(dir1, backOfPelvic, dir2)),
			newBoundLineTo(backOfPelvic)
			// not closed
		);
		leg.setStroke(Color.BLACK);
		leg.setFill(Color.BLUE);
		leg.setStrokeWidth(1);
		return leg;
	}
	
}
