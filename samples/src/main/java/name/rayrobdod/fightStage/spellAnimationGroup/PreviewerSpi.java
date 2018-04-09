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

import java.util.Arrays;
import java.util.List;

import javafx.scene.paint.Color;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.previewer.spi.NameSupplierPair;
import name.rayrobdod.fightStage.previewer.spi.SpellAnimationGroups;
import name.rayrobdod.fightStage.spellAnimationGroup.electricty.*;

/**
 * An enumeration of {@link SpellAnimationGroup}s in this project
 */
public final class PreviewerSpi implements SpellAnimationGroups {
	
	@Override public List<NameSupplierPair<SpellAnimationGroup>> get() {
		return Arrays.asList(
			  new NameSupplierPair<>("Physical Hit", () -> new PhysicalHit())
			, new NameSupplierPair<>("Arrow", () -> new Arrow())
			, new NameSupplierPair<>("Dark", () -> new Dark())
			, new NameSupplierPair<>("Fireball", () -> new Fireball())
			, new NameSupplierPair<>("Lightning (Fade)", () -> new BasicElectricitySpellAnimationGroup(new SkyBoltPoints(), FadeElectricAnimationFactory::new))
			, new NameSupplierPair<>("Lightning (Dissipate)", () -> new BasicElectricitySpellAnimationGroup(new SkyBoltPoints(), DissipateElectricAnimationFactory::new))
			, new NameSupplierPair<>("Spark (Fade)", () -> new BasicElectricitySpellAnimationGroup(new ChainPoints(), FadeElectricAnimationFactory::new))
			, new NameSupplierPair<>("Spark (Dissipate)", () -> new BasicElectricitySpellAnimationGroup(new ChainPoints(), DissipateElectricAnimationFactory::new))
			, new NameSupplierPair<>("Radial Lightning", () -> new RadialLightning())
			, new NameSupplierPair<>("Thunderstorm", () -> new ThunderStorm())
			, new NameSupplierPair<>("Crescent Wind", () -> new CrescentWind())
			, new NameSupplierPair<>("Tornado", () -> new Tornado())
			, new NameSupplierPair<>("MultiSparkle", () -> new MultiSparkle())
			, new NameSupplierPair<>("SkyBeam", () -> new SkyBeam())
			, new NameSupplierPair<>("Not-Naga", () -> new LightBurst())
			, new NameSupplierPair<>("LightSword", () -> new LightSword())
			, new NameSupplierPair<>("HealCoil", () -> new HealCoil())
			, new NameSupplierPair<>("Lazor (Blue)", () -> new Lazor(Color.BLUE))
			, new NameSupplierPair<>("Lazor (Red)", () -> new Lazor(Color.RED))
		);
	}

}
