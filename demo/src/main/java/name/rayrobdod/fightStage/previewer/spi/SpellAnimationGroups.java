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
