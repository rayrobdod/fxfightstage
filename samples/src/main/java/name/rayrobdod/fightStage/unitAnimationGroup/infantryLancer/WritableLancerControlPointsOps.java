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

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

/**
 * Partial Classes! :D
 */
interface WritableLancerControlPointsOps {
	WritableLancerControlPoints self();
	
	static final double LANCE_HALFWIDTH = 3;
	
	default Path createStickFigure() {
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
	
	default Group createLance() {
		final WritablePoint2dValue center = self().lanceCenter;
		final Point2dBinding direction = center.subtract(self().lanceControl).normalize();
		final Point2dBinding perp = direction.perpendicular();
		
		
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
}
