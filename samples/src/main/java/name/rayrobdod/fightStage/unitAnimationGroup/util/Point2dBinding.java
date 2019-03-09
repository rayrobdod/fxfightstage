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

import java.util.function.Supplier;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Point2D;

public abstract class Point2dBinding extends ObjectBinding<Point2D> implements Point2dExpression {

	/**
	 * @note Unlike {@link Bindings}, func is a {@link Supplier}, not a {@link java.util.concurrent.Callable}, meaning no exceptions allowed
	 */
	public static Point2dBinding createPoint2dBinding(Supplier<Point2D> func, Observable... dependencies) {
		return new Point2dBinding() {
			{
				super.bind(dependencies);
			}

			protected Point2D computeValue() {
				return func.get();
			}
		};
	}
}
