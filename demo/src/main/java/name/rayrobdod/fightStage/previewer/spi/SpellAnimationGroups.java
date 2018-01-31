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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import name.rayrobdod.fightStage.SpellAnimationGroup;

public interface SpellAnimationGroups {
	
	public List<NameSupplierPair<SpellAnimationGroup>> get();
	
	public static List<NameSupplierPair<SpellAnimationGroup>> getAll() {
		ServiceLoader<SpellAnimationGroups> services = ServiceLoader.load(SpellAnimationGroups.class);
		List<NameSupplierPair<SpellAnimationGroup>> retval = new ArrayList<>();
		services.forEach(x -> retval.addAll(x.get()));
		return retval;
	}
}
