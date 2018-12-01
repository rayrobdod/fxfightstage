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

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer.Point2dBinding.createPoint2dBinding;

import java.util.ArrayList;
import java.util.function.Supplier;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableValue;
import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

import name.rayrobdod.fightStage.PathElements;

/**
 * Fluent bindings for Point2D ObservableValues.
 * 
 * Most of the methods mirror those in {@link javafx.geometry.Point2D}
 */
interface Point2dExpression extends Observable, ObservableObjectValue<Point2D> {
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

/**
 * A wrapper for a Point2D that is writable, observable, and has the fluent binding methods
 */
final class WritablePoint2dValue implements WritableValue<Point2D>, ObservableObjectValue<Point2D>, Point2dExpression {
	private Point2D value;
	private final ArrayList<ChangeListener<? super Point2D>> changeListeners;
	private final ArrayList<InvalidationListener> invalidationListeners;
	
	public WritablePoint2dValue() {
		this.value = Point2D.ZERO;
		this.changeListeners = new ArrayList<>();
		this.invalidationListeners = new ArrayList<>();
	}
	
	@Override public Point2D getValue() {return value;}
	@Override public Point2D get() {return value;}
	@Override public void setValue(final Point2D newval) {
		final Point2D oldval = this.value;
		this.value = newval;
		changeListeners.forEach(x -> x.changed(this, oldval, newval));
		invalidationListeners.forEach(x -> x.invalidated(this));
	}
	
	public final WritableDoubleValue writableX = new WritableDoubleValue() {
		public double get() {return WritablePoint2dValue.this.value.getX();}
		public void set(final double newx) {
			final Point2D newVal = new Point2D(newx, WritablePoint2dValue.this.value.getY());
			WritablePoint2dValue.this.setValue(newVal);
		}
		public Double getValue() {return this.get();}
		public void setValue(Number newy) {this.set(newy.doubleValue());}
	};
	
	public final WritableDoubleValue writableY = new WritableDoubleValue() {
		public double get() {return WritablePoint2dValue.this.value.getY();}
		public void set(final double newy) {
			final Point2D newVal = new Point2D(WritablePoint2dValue.this.value.getX(), newy);
			WritablePoint2dValue.this.setValue(newVal);
		}
		public Double getValue() {return this.get();}
		public void setValue(Number newy) {this.set(newy.doubleValue());}
	};
	
	@Override public void addListener(InvalidationListener ex) {this.invalidationListeners.add(ex);}
	@Override public void addListener(ChangeListener<? super Point2D> ex) {this.changeListeners.add(ex);}
	@Override public void removeListener(InvalidationListener ex) {this.invalidationListeners.remove(ex);}
	@Override public void removeListener(ChangeListener<? super Point2D> ex) {this.changeListeners.remove(ex);}
}

abstract class Point2dBinding extends ObjectBinding<Point2D> implements Point2dExpression {
	
	/**
	 * @note Unlike {@link Bindings}, func is a {@link Supplier}, not a {@link java.util.concurrent.Callable}, meaning no exceptions allowed
	 */
	static Point2dBinding createPoint2dBinding(Supplier<Point2D> func, Observable... dependencies) {
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

final class Point2dPathElements {
	private Point2dPathElements() {}
	
	static MoveTo newBoundMoveTo(final Point2dExpression p) {return PathElements.newBoundMoveTo(p.x(), p.y());}
	static LineTo newBoundLineTo(final Point2dExpression p) {return PathElements.newBoundLineTo(p.x(), p.y());}
	static CubicCurveTo newBoundCubicCurveTo(final Point2dExpression c1, final Point2dExpression c2, final Point2dExpression p) {
		return PathElements.newBoundCubicCurveTo(c1.x(), c1.y(), c2.x(), c2.y(), p.x(), p.y());
	}
}

/**
 * Static methods related to {@link Point2D}s
 */
final class Point2Ds {
	private Point2Ds() {}
	
	/**
	 * (which java version allows interfaces to have private static methods?)
	 * Returns a point on the interception of two lines (or the midpoint of p1 and p2 if there is not one unique such point)
	 * @param p1 a point on the first line
	 * @param p2 a point on the second line
	 * @param v1 the direction of the first line
	 * @param v2 the direction on the second line
	 */
	public static Point2D interception(Point2D p1, Point2D v1, Point2D p2, Point2D v2) {
		// `getX == 0` indicates a vertical line
		if (v1.getX() == 0 && v2.getX() == 0) {
			return p1.midpoint(p2);
		} else if (v1.getX() == 0) {
			final double m2 = v2.getY() / v2.getX();
			final double b2 = p2.getY() - m2 * p2.getX();
			return new Point2D(p1.getX(), m2 * p1.getX() + b2);
		} else if (v2.getX() == 0) {
			final double m1 = v1.getY() / v1.getX();
			final double b1 = p1.getY() - m1 * p1.getX();
			return new Point2D(p2.getX(), m1 * p2.getX() + b1);
		} else {
			final double m1 = v1.getY() / v1.getX();
			final double m2 = v2.getY() / v2.getX();
			final double b1 = p1.getY() - m1 * p1.getX();
			final double b2 = p2.getY() - m2 * p2.getX();
			
			if (m1 == m2) {
				return p1.midpoint(p2);
			} else {
				final double x = (b2 - b1) / (m1 - m2);
				final double y = m1 * x + b1;
				return new Point2D(x, y);
			}
		}
	}
	
	/**
	 * A Point2D representing the vector with the specified radius and angle
	 */
	public static Point2D polar(double radius, double angle) {
		return new Point2D(radius * Math.cos(angle), radius * Math.sin(angle));
	}
}
