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

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static name.rayrobdod.fightStage.unitAnimationGroup.util.Point2dBinding.createPoint2dBinding;

import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

/**
 * Fluent bindings for Point2D ObservableValues.
 *
 * Most of the methods mirror those in {@link javafx.geometry.Point2D}
 */
public interface Point2dExpression extends Observable, ObservableObjectValue<Point2D> {
	Point2D getValue();

	default Point2dBinding add(Point2D rhs) {
		return createPoint2dBinding(() -> this.getValue().add(rhs), this);
	}

	default Point2dBinding add(ObservableValue<Point2D> rhs) {
		return createPoint2dBinding(() -> this.getValue().add(rhs.getValue()), this, rhs);
	}

	default Point2dBinding subtract(ObservableValue<Point2D> rhs) {
		return createPoint2dBinding(() -> this.getValue().subtract(rhs.getValue()), this, rhs);
	}

	default Point2dBinding multiply(double rhs) {
		return createPoint2dBinding(() -> this.getValue().multiply(rhs), this);
	}

	default Point2dBinding multiply(ObservableDoubleValue rhs) {
		return createPoint2dBinding(() -> this.getValue().multiply(rhs.get()), this, rhs);
	}

	default Point2dBinding midpoint(ObservableValue<Point2D> rhs) {
		return createPoint2dBinding(() -> this.getValue().midpoint(rhs.getValue()), this, rhs);
	}

	default DoubleBinding angle(ObservableValue<Point2D> p1, ObservableValue<Point2D> p2) {
		return createDoubleBinding(() -> this.getValue().angle(p1.getValue(), p2.getValue()), this, p1, p2);
	}

	/**
	 * Returns a point on the interception of two lines (or the midpoint of p1 and p2 if there is not one unique such point)
	 * @param this a point on the first line
	 * @param p2 a point on the second line
	 * @param v1 the direction of the first line
	 * @param v2 the direction on the second line
	 */
	default Point2dBinding interception(ObservableValue<Point2D> v1, ObservableValue<Point2D> p2, ObservableValue<Point2D> v2) {
		return createPoint2dBinding(() -> Point2Ds.interception(this.getValue(), v1.getValue(), p2.getValue(), v2.getValue()), this, v1, p2, v2);
	}

	default Point2dBinding interception(ObservableValue<Point2D> v1, Point2D p2, Point2D v2) {
		return createPoint2dBinding(() -> Point2Ds.interception(this.getValue(), v1.getValue(), p2, v2), this, v1);
	}

	default DoubleBinding magnitude() {
		return createDoubleBinding(() -> this.getValue().magnitude(), this);
	}

	default Point2dBinding normalize() {
		return createPoint2dBinding(() -> this.getValue().normalize(), this);
	}

	default Point2dBinding perpendicular() {
		return createPoint2dBinding(() -> new Point2D(-this.getValue().getY(), this.getValue().getX()), this);
	}

	default Point2dBinding negate() {
		return createPoint2dBinding(() -> new Point2D(-this.getValue().getX(), -this.getValue().getY()), this);
	}

	default Point2dBinding withX(double newX) {
		return createPoint2dBinding(() -> new Point2D(newX, this.getValue().getY()), this);
	}

	default Point2dBinding withY(double newY) {
		return createPoint2dBinding(() -> new Point2D(this.getValue().getX(), newY), this);
	}

	default DoubleBinding x() {
		return createDoubleBinding(() -> this.getValue().getX(), this);
	}

	default DoubleBinding y() {
		return createDoubleBinding(() -> this.getValue().getY(), this);
	}

	public static Point2dExpression apply(ObservableValue<Point2D> wrap) {
		if (wrap instanceof Point2dExpression) {
			return (Point2dExpression) wrap;
		} else {
			return createPoint2dBinding(() -> wrap.getValue(), wrap);
		}
	}
}
