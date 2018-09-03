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
package name.rayrobdod.fightStage.unitAnimationGroup;

import java.util.Arrays;
import java.util.List;

import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.previewer.spi.NameSupplierPair;
import name.rayrobdod.fightStage.previewer.spi.UnitAnimationGroups;
import name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer.InfantryLancer;

/**
 * An enumeration of {@link UnitAnimationGroup}s in this project
 */
public final class PreviewerSpi implements UnitAnimationGroups {
	
	@Override public List<NameSupplierPair<UnitAnimationGroup>> get() {
		return Arrays.asList(
			  new NameSupplierPair<>("SwordGuy", () -> new SwordGuy())
			, new NameSupplierPair<>("LanceGuy", () -> new InfantryLancer())
			, new NameSupplierPair<>("MageGuy", () -> new MageGuy())
			, new NameSupplierPair<>("BowGuy", () -> new BowGuy())
			, new NameSupplierPair<>("Sandbag/(medium)", () -> new Sandbag(60, 90))
			, new NameSupplierPair<>("Sandbag/(small)", () -> new Sandbag(30, 45))
			, new NameSupplierPair<>("Sandbag/(large)", () -> new Sandbag(90, 150))
		);
	}

}
