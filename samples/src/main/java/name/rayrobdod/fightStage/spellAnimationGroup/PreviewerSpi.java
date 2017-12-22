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
			, new NameSupplierPair<>("Not-Naga", () -> new LightBurst())
			, new NameSupplierPair<>("LightSword", () -> new LightSword())
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
