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
			, new NameSupplierPair<>("Not-Naga", () -> new LightBurst())
			, new NameSupplierPair<>("LightSword", () -> new LightSword())
			, new NameSupplierPair<>("HealCoil", () -> new HealCoil())
			, new NameSupplierPair<>("Lazor (Blue)", () -> new Lazor(Color.BLUE))
			, new NameSupplierPair<>("Lazor (Red)", () -> new Lazor(Color.RED))
		);
	}

}
