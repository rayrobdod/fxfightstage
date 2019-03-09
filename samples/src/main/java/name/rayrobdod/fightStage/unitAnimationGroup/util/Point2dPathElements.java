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
package name.rayrobdod.fightStage.unitAnimationGroup.util;

import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

import name.rayrobdod.fightStage.PathElements;

public final class Point2dPathElements {
	private Point2dPathElements() {}

	public static MoveTo newBoundMoveTo(final Point2dExpression p) {return PathElements.newBoundMoveTo(p.x(), p.y());}
	public static LineTo newBoundLineTo(final Point2dExpression p) {return PathElements.newBoundLineTo(p.x(), p.y());}
	public static CubicCurveTo newBoundCubicCurveTo(final Point2dExpression c1, final Point2dExpression c2, final Point2dExpression p) {
		return PathElements.newBoundCubicCurveTo(c1.x(), c1.y(), c2.x(), c2.y(), p.x(), p.y());
	}
}
