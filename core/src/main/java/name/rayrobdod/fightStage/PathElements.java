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
package name.rayrobdod.fightStage;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.VLineTo;

/**
 * A collection of static functions that create {@link PathElement}s
 */
public final class PathElements {
	private PathElements() {}
	
	/** Returns a MoveTo whose properties are already bound to the given observables */
	public static MoveTo newBoundMoveTo(
		ObservableDoubleValue x, ObservableDoubleValue y
	) {
		MoveTo retval = new MoveTo();
		retval.xProperty().bind(x);
		retval.yProperty().bind(y);
		return retval;
	}
	
	/** Returns a LineTo whose properties are already bound to the given observables */
	public static LineTo newBoundLineTo(
		ObservableDoubleValue x, ObservableDoubleValue y
	) {
		LineTo retval = new LineTo();
		retval.xProperty().bind(x);
		retval.yProperty().bind(y);
		return retval;
	}
	
	/** Returns a HLineTo whose properties are already bound to the given observables */
	public static HLineTo newBoundHLineTo(
		ObservableDoubleValue x
	) {
		HLineTo retval = new HLineTo();
		retval.xProperty().bind(x);
		return retval;
	}
	
	/** Returns a VLineTo whose properties are already bound to the given observables */
	public static VLineTo newBoundVLineTo(
		ObservableDoubleValue y
	) {
		VLineTo retval = new VLineTo();
		retval.yProperty().bind(y);
		return retval;
	}
	
	/** Returns a QuadCurveTo whose properties are already bound to the given observables */
	public static QuadCurveTo newBoundQuadCurveTo(
		ObservableDoubleValue cx, ObservableDoubleValue cy,
		ObservableDoubleValue x, ObservableDoubleValue y
	) {
		QuadCurveTo retval = new QuadCurveTo();
		retval.controlXProperty().bind(cx);
		retval.controlYProperty().bind(cy);
		retval.xProperty().bind(x);
		retval.yProperty().bind(y);
		return retval;
	}
	
	/** Returns a CubicCurveTo whose properties are already bound to the given observables */
	public static CubicCurveTo newBoundCubicCurveTo(
		ObservableDoubleValue c1x, ObservableDoubleValue c1y,
		ObservableDoubleValue c2x, ObservableDoubleValue c2y,
		ObservableDoubleValue x, ObservableDoubleValue y
	) {
		CubicCurveTo retval = new CubicCurveTo();
		retval.controlX1Property().bind(c1x);
		retval.controlY1Property().bind(c1y);
		retval.controlX2Property().bind(c2x);
		retval.controlY2Property().bind(c2y);
		retval.xProperty().bind(x);
		retval.yProperty().bind(y);
		return retval;
	}
}
