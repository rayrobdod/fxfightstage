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
package name.rayrobdod.fightStage.spellAnimationGroup.electricty;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.value.WritableValue;

/**
 * A pair consisting of a WritableValue and a value that can
 * be written to the WritableValue.
 */
public final class WritableValuePair<T> {
	private final WritableValue<T> writable;
	private final T value;
	
	public WritableValuePair(
		WritableValue<T> writable,
		T value
	) {
		this.writable = writable;
		this.value = value;
	}
	
	/**
	 * Sets the WritableValue's value to the value given in this's constructor
	 */
	public void apply() { writable.setValue(value); }
	
	public KeyValue toKeyValue(Interpolator interp) {
		return new KeyValue(writable, value, interp);
	}
}
