package name.rayrobdod.fightStage.previewer.spi;

import java.util.function.Supplier;

/**
 * A pair of a Supplier of objects and a descriptor of those objects.
 */
public final class NameSupplierPair<E> {
	public final String displayName;
	public final Supplier<E> supplier;
	
	public NameSupplierPair(
		String displayName,
		Supplier<E> supplier
	) {
		this.displayName = displayName;
		this.supplier = supplier;
	}
}
