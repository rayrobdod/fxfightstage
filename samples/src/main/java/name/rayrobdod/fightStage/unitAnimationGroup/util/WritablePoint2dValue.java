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

import java.util.ArrayList;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableValue;
import javafx.geometry.Point2D;

/**
 * A wrapper for a Point2D that is writable, observable, and has the fluent binding methods
 */
public final class WritablePoint2dValue implements WritableValue<Point2D>, ObservableObjectValue<Point2D>, Point2dExpression {
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
		new ArrayList<>(changeListeners).forEach(x -> x.changed(this, oldval, newval));
		new ArrayList<>(invalidationListeners).forEach(x -> x.invalidated(this));
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
