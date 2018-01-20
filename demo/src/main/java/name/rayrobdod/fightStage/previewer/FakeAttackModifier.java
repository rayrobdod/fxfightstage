package name.rayrobdod.fightStage.previewer;

import java.util.Optional;

import name.rayrobdod.fightStage.AttackModifier;

public final class FakeAttackModifier implements AttackModifier {
	private final String displayName;
	
	/**
	 * @param displayName nullable
	 */
	public FakeAttackModifier(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public Optional<String> getDisplayName() {
		return Optional.of(this.displayName);
	}
}
