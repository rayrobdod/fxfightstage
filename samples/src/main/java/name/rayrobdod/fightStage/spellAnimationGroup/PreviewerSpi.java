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

public final class PreviewerSpi implements SpellAnimationGroups {
	
	@Override public List<NameSupplierPair<SpellAnimationGroup>> get() {
		return Arrays.asList(
			  new NameSupplierPair<>("Physical Hit", () -> new PhysicalHit())
			, new NameSupplierPair<>("Arrow", () -> new Arrow())
			, new NameSupplierPair<>("Dark", () -> new Dark())
			, new NameSupplierPair<>("Fireball", () -> new Fireball())
			, new NameSupplierPair<>("Lightning", () -> new Lightning())
			, new NameSupplierPair<>("Spark", () -> new Spark())
			, new NameSupplierPair<>("Spark V2", () -> new SparkWithBetterFade())
			, new NameSupplierPair<>("Not-Naga", () -> new LightBurst())
			, new NameSupplierPair<>("LightSword", () -> new LightSword())
			, new NameSupplierPair<>("HealCoil", () -> new HealCoil())
			, new NameSupplierPair<>("Lazor (Blue)", () -> new Lazor(Color.BLUE))
			, new NameSupplierPair<>("Lazor (Red)", () -> new Lazor(Color.RED))
			, new NameSupplierPair<>("<fxml> Dark", () -> buildFromFxml("/name/rayrobdod/fightStage/spellAnimationGroup/dark.fxml"))
		);
	}
	
	private static SpellAnimationGroup buildFromFxml(String path) {
		try {
			final java.net.URL url = PreviewerSpi.class.getResource(path);
			final Object obj = javafx.fxml.FXMLLoader.load(url);
			return (SpellAnimationGroup) obj;
		} catch (java.io.IOException e) {
			throw new AssertionError("Failed to read file " + path, e);
		} catch (java.lang.ClassCastException e) {
			throw new AssertionError("File contained object of incorrect type " + path, e);
		}
	}
}
