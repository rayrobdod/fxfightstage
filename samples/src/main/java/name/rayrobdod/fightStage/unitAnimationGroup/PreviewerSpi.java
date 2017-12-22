package name.rayrobdod.fightStage.unitAnimationGroup;

import java.util.Arrays;
import java.util.List;

import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.previewer.spi.NameSupplierPair;
import name.rayrobdod.fightStage.previewer.spi.UnitAnimationGroups;

public final class PreviewerSpi implements UnitAnimationGroups {
	
	@Override public List<NameSupplierPair<UnitAnimationGroup>> get() {
		return Arrays.asList(
			  new NameSupplierPair<>("SwordGuy", () -> new SwordGuy())
			, new NameSupplierPair<>("MageGuy", () -> new MageGuy())
			, new NameSupplierPair<>("BowGuy", () -> new BowGuy())
		);
	}

}
