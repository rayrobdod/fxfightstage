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

import static name.rayrobdod.fightStage.unitAnimationGroup.util.Point2dPathElements.newBoundLineTo;
import static name.rayrobdod.fightStage.unitAnimationGroup.util.Point2dPathElements.newBoundMoveTo;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

import name.rayrobdod.fightStage.unitAnimationGroup.util.*;

final class Skin {
	public final WritableBoneControls bones;

	public Skin(WritableBoneControls bones) {
		this.bones = bones;
	}

	public Group build() {
		return new Group(
			  this.createStickFigure()
		);
	}

	private Node createStickFigure() {
		final Path retval = new Path(
			  newBoundMoveTo(bones.leftFoot())
			, newBoundLineTo(bones.leftKnee())
			, newBoundLineTo(bones.leftPelvic())
			, newBoundLineTo(bones.rightPelvic())
			, newBoundLineTo(bones.rightKnee())
			, newBoundLineTo(bones.rightFoot())
			, newBoundMoveTo(bones.leftHand())
			, newBoundLineTo(bones.leftElbow())
			, newBoundLineTo(bones.leftShoulder())
			, newBoundLineTo(bones.rightShoulder())
			, newBoundLineTo(bones.rightElbow())
			, newBoundLineTo(bones.rightHand())
			, newBoundMoveTo(bones.centerPelvic())
			, newBoundLineTo(bones.neck())
		);
		retval.setStroke(Color.BLACK);
		retval.setFill(Color.TRANSPARENT);
		retval.setStrokeWidth(2);
		return retval;
	}
}
