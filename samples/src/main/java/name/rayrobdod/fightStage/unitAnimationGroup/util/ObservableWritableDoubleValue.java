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
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.WritableDoubleValue;

/**
 */
public final class ObservableWritableDoubleValue implements ObservableDoubleValue, WritableDoubleValue {
	private double backing;
	private final ArrayList<ChangeListener<? super Number>> changeListeners;
	private final ArrayList<InvalidationListener> invalidationListeners;

	public ObservableWritableDoubleValue() {
		this.backing = 0.0;
		this.changeListeners = new ArrayList<>();
		this.invalidationListeners = new ArrayList<>();
	}

	@Override public double get() {return this.backing;}
	@Override public void set(double newval) {
		double oldval = this.backing;
		this.backing = newval;
		new ArrayList<>(changeListeners).forEach(x -> x.changed(this, oldval, newval));
		new ArrayList<>(invalidationListeners).forEach(x -> x.invalidated(this));
	}
	@Override public Number getValue() {return this.get();}
	@Override public void setValue(Number v) {this.set(v.doubleValue());}
	@Override public double doubleValue() {return this.get();}
	@Override public float floatValue() {return (float) this.get();}
	@Override public int intValue() {return (int) this.get();}
	@Override public long longValue() {return (long) this.get();}

	@Override public void addListener(InvalidationListener ex) {this.invalidationListeners.add(ex);}
	@Override public void addListener(ChangeListener<? super Number> ex) {this.changeListeners.add(ex);}
	@Override public void removeListener(InvalidationListener ex) {this.invalidationListeners.remove(ex);}
	@Override public void removeListener(ChangeListener<? super Number> ex) {this.changeListeners.remove(ex);}
}
