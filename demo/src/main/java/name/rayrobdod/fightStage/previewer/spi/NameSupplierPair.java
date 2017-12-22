package name.rayrobdod.fightStage.previewer.spi;

import java.util.function.Supplier;

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
