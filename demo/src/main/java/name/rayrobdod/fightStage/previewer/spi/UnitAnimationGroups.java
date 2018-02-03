package name.rayrobdod.fightStage.previewer.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import name.rayrobdod.fightStage.UnitAnimationGroup;

/**
 * A {@link ServiceLoader service} that enumerates UnitAnimationGroup
 */
public interface UnitAnimationGroups {
	
	/**
	 * A sequence of {@link UnitAnimationGroup}s
	 */
	public List<NameSupplierPair<UnitAnimationGroup>> get();
	
	/**
	 * The concatenation of sequences from every service provider found on the classpath.
	 */
	public static List<NameSupplierPair<UnitAnimationGroup>> getAll() {
		ServiceLoader<UnitAnimationGroups> services = ServiceLoader.load(UnitAnimationGroups.class);
		List<NameSupplierPair<UnitAnimationGroup>> retval = new ArrayList<>();
		services.forEach(x -> retval.addAll(x.get()));
		return retval;
	}
}
