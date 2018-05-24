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
			, new NameSupplierPair<>("Dark/Dark", () -> new Dark())
			, new NameSupplierPair<>("Dark/Nosferatu", () -> new Nosferatu())
			, new NameSupplierPair<>("Fire/Fireball", () -> new Fireball())
			, new NameSupplierPair<>("Electric/Lightning (Fade)", () -> new BasicElectricitySpellAnimationGroup(new SkyBoltPoints(), FadeElectricAnimationFactory::new))
			, new NameSupplierPair<>("Electric/Lightning (Dissipate)", () -> new BasicElectricitySpellAnimationGroup(new SkyBoltPoints(), DissipateElectricAnimationFactory::new))
			, new NameSupplierPair<>("Electric/Spark (Fade)", () -> new BasicElectricitySpellAnimationGroup(new ChainPoints(), FadeElectricAnimationFactory::new))
			, new NameSupplierPair<>("Electric/Spark (Dissipate)", () -> new BasicElectricitySpellAnimationGroup(new ChainPoints(), DissipateElectricAnimationFactory::new))
			, new NameSupplierPair<>("Electric/Radial Lightning", () -> new RadialLightning())
			, new NameSupplierPair<>("Electric/Thunderstorm", () -> new ThunderStorm())
			, new NameSupplierPair<>("Wind/Crescent Wind", () -> new CrescentWind())
			, new NameSupplierPair<>("Wind/Tornado", () -> new Tornado())
			, new NameSupplierPair<>("Light/MultiSparkle", () -> new MultiSparkle())
			, new NameSupplierPair<>("Light/Quarantine", () -> new Quarantine())
			, new NameSupplierPair<>("Light/SkyBeam", () -> new SkyBeam())
			, new NameSupplierPair<>("Light/Not-Naga", () -> new LightBurst())
			, new NameSupplierPair<>("Light/LightSword", () -> new LightSword())
			, new NameSupplierPair<>("Light/HealCoil", () -> new HealCoil())
			, new NameSupplierPair<>("Lazor/(Blue)", () -> new Lazor(Color.BLUE))
			, new NameSupplierPair<>("Lazor/(Red)", () -> new Lazor(Color.RED))
			, new NameSupplierPair<>("Lazor/(Lime)", () -> new Lazor(Color.LIME))
		);
	}

}
