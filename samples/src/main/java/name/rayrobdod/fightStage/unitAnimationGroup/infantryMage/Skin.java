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

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Path;

import name.rayrobdod.fightStage.unitAnimationGroup.util.*;

final class Skin {
	public final WritableBoneControls bones;

	private static final double STROKE_HALFWIDTH = 0.5;
	private static final double LEG_HALFWIDTH = 5;
	private static final double ARM_HALFWIDTH = 5;
	private static final double HEAD_HALFWIDTH = 15;

	private static final Color SKIN_COLOR = Color.PINK;
	private static final Color PANT_COLOR = Color.BLUE;
	private static final Color SHIRT_COLOR = Color.RED;

	public Skin(WritableBoneControls bones) {
		this.bones = bones;
	}

	public Group build() {
		return new Group(
			this.stickFigure(),

			this.bone(bones.leftFoot(), bones.leftKnee(), LEG_HALFWIDTH, PANT_COLOR),
			this.pivot(bones.leftKnee(), LEG_HALFWIDTH, PANT_COLOR),
			this.bone(bones.leftKnee(), bones.leftPelvic(), LEG_HALFWIDTH, PANT_COLOR),

			this.bone(bones.rightFoot(), bones.rightKnee(), LEG_HALFWIDTH, PANT_COLOR),
			this.pivot(bones.rightKnee(), LEG_HALFWIDTH, PANT_COLOR),
			this.bone(bones.rightKnee(), bones.rightPelvic(), LEG_HALFWIDTH, PANT_COLOR),

			this.torso(),
			this.pantSeat(),

			this.pivot(bones.leftHand(), ARM_HALFWIDTH, SKIN_COLOR),
			this.bone(bones.leftHand(), bones.leftElbow(), ARM_HALFWIDTH, SHIRT_COLOR),
			this.pivot(bones.leftElbow(), ARM_HALFWIDTH, SHIRT_COLOR),
			this.bone(bones.leftElbow(), bones.leftShoulder(), ARM_HALFWIDTH, SHIRT_COLOR),

			this.pivot(bones.rightHand(), ARM_HALFWIDTH, SKIN_COLOR),
			this.bone(bones.rightHand(), bones.rightElbow(), ARM_HALFWIDTH, SHIRT_COLOR),
			this.pivot(bones.rightElbow(), ARM_HALFWIDTH, SHIRT_COLOR),
			this.bone(bones.rightElbow(), bones.rightShoulder(), ARM_HALFWIDTH, SHIRT_COLOR),

			new javafx.scene.shape.Circle()
		);
	}

	private Node stickFigure() {
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

	private Node pantSeat() {
		final int outset = 0;
		final Color fill = Color.BLUE;
		final Point2dExpression left = bones.leftPelvic();
		final Point2dExpression right = bones.rightPelvic();

		final Path leg = new Path(
			newBoundMoveTo(left.add(new Point2D(LEG_HALFWIDTH + outset, 2 + outset))),
			newBoundLineTo(left.add(new Point2D(LEG_HALFWIDTH + outset, -10 - outset))),
			newBoundLineTo(right.add(new Point2D(-LEG_HALFWIDTH - outset, -10 - outset))),
			newBoundLineTo(right.add(new Point2D(-LEG_HALFWIDTH - outset, 2 + outset))),
			new ClosePath()
		);
		leg.setFill(fill);
		leg.setStrokeWidth(0);
		return leg;
	}

	private Node torso() {
		final double outset = 0;
		final Color fill = Color.RED;

		final Point2dExpression topDir = bones.leftShoulder().subtract(bones.rightShoulder()).normalize();
		final Point2dExpression botDir = bones.leftPelvic().subtract(bones.rightPelvic()).normalize();
		final Point2dExpression leftDir = bones.leftShoulder().subtract(bones.leftPelvic()).normalize();
		final Point2dExpression rightDir = bones.rightShoulder().subtract(bones.rightPelvic()).normalize();

		final Point2dExpression topLeftSide = bones.leftShoulder().add(topDir.multiply(ARM_HALFWIDTH + outset));
		final Point2dExpression topRightSide = bones.rightShoulder().add(topDir.multiply(-ARM_HALFWIDTH - outset));
		final Point2dExpression topLeftTop = bones.leftShoulder().add(leftDir.multiply(ARM_HALFWIDTH + outset));
		final Point2dExpression topRightTop = bones.rightShoulder().add(rightDir.multiply(ARM_HALFWIDTH + outset));
		final Point2dExpression botLeft = bones.leftPelvic().add(botDir.multiply(LEG_HALFWIDTH + outset));
		final Point2dExpression botRight = bones.rightPelvic().add(botDir.multiply(-LEG_HALFWIDTH - outset));

		final Path retval = new Path(
			newBoundMoveTo(botRight),
			newBoundLineTo(botLeft),
			newBoundLineTo(topLeftSide),
			newBoundLineTo(topLeftTop),
			newBoundLineTo(topRightTop),
			newBoundLineTo(topRightSide),
			new ClosePath()
		);
		retval.setFill(fill);
		retval.setStrokeWidth(0);
		return retval;
	}

	private static Node pivot(final Point2dExpression center, final double radius, final Color fill) {
		javafx.scene.shape.Circle retval = new javafx.scene.shape.Circle();
		retval.centerXProperty().bind(center.x());
		retval.centerYProperty().bind(center.y());
		retval.radiusProperty().set(radius);
		retval.setFill(fill);
		retval.setStrokeWidth(0);
		return retval;
	}

	private static Node bone(final Point2dExpression a, final Point2dExpression b, final double halfWidth, final Color fill) {
		final Point2dExpression dir = b.subtract(a).normalize();
		final Point2dExpression perp = dir.perpendicular();
		final Point2dExpression perpWidth = perp.multiply(halfWidth);

		final Path retval = new Path(
			newBoundMoveTo(a.add(perpWidth)),
			newBoundLineTo(a.subtract(perpWidth)),
			newBoundLineTo(b.subtract(perpWidth)),
			newBoundLineTo(b.add(perpWidth)),
			new ClosePath()
		);
		retval.setFill(fill);
		retval.setStrokeWidth(0);
		return retval;
	}
}
