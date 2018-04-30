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
package name.rayrobdod.fightStage.spellAnimationGroup;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;

/**
 * A WritableValue which sets and gets the values from a given ObservableList.
 * <p>
 * Mostly intended for KeyValue animations. Especially since attempts to use
 * `ListProperty` failed, especially with multiple ListProperties in the same
 * animation, or was unduly cumbersome.
 */
public final class WritableObservableListWrapper<T> implements WritableValue<List<T>> {
	private final ObservableList<T> backing;
	
	public WritableObservableListWrapper(
		ObservableList<T> backing
	) {
		this.backing = backing;
	}
	
	/**
	 * Clears the backing list, then copies elements from the parameter to the
	 * backing list.
	 * @see ObservableList#setAll
	 */
	public void setValue(List<T> vals) {
		backing.setAll(vals);
	}
	
	public List<T> getValue() {
		return java.util.Collections.unmodifiableList(new ArrayList<>(backing));
	}
}
