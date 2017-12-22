package name.rayrobdod.fightStage.previewer.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import name.rayrobdod.fightStage.UnitAnimationGroup;

public interface UnitAnimationGroups {
	
	public List<NameSupplierPair<UnitAnimationGroup>> get();
	
	public static List<NameSupplierPair<UnitAnimationGroup>> getAll() {
		ServiceLoader<UnitAnimationGroups> services = ServiceLoader.load(UnitAnimationGroups.class);
		List<NameSupplierPair<UnitAnimationGroup>> retval = new ArrayList<>();
		services.forEach(x -> retval.addAll(x.get()));
		return retval;
	}
}
