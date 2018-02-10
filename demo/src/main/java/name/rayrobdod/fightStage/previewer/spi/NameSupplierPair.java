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
package name.rayrobdod.fightStage.previewer.spi;

import java.util.function.Supplier;

/**
 * A pair of a Supplier of objects and a descriptor of those objects.
 * <p>
 * This uses a Supplier, as the `E`s used in {@link SpellAnimationGroups} and
 * {@link UnitAnimationGroups} involve {@link javafx.scene.Node}s, which are
 * not allowed to appear in a scene graph multiple times, and might be used
 * multiple times (i.e. for the left and right sides of a battle simultaneously)
 */
public final class NameSupplierPair<E> {
	/** The name used in a UI to represent the objects */
	public final String displayName;
	/** A supplier of objects */
	public final Supplier<E> supplier;
	
	public NameSupplierPair(
		String displayName,
		Supplier<E> supplier
	) {
		this.displayName = displayName;
		this.supplier = supplier;
	}
}
