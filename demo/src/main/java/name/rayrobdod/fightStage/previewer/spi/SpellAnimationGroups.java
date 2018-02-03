package name.rayrobdod.fightStage.previewer.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A {@link ServiceLoader service} that enumerates SpellAnimationGroups
 */
public interface SpellAnimationGroups {
	
	/**
	 * A sequence of {@link SpellAnimationGroup}s
	 */
	public List<NameSupplierPair<SpellAnimationGroup>> get();
	
	/**
	 * The concatenation of sequences from every service provider found on the classpath.
	 */
	public static List<NameSupplierPair<SpellAnimationGroup>> getAll() {
		ServiceLoader<SpellAnimationGroups> services = ServiceLoader.load(SpellAnimationGroups.class);
		List<NameSupplierPair<SpellAnimationGroup>> retval = new ArrayList<>();
		services.forEach(x -> retval.addAll(x.get()));
		return retval;
	}
}
