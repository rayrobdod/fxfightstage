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
 * Fluent bindings for Point2D ObservableValues
 */
interface Point2dExpression extends javafx.beans.Observable {
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

abstract class Point2dBinding extends javafx.beans.binding.ObjectBinding<javafx.geometry.Point2D> implements Point2dExpression {
	
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
