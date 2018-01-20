package name.rayrobdod.fightStage;

import java.util.Optional;

/**
 * Represents a skill that changes the properties of an attack.
 * 
 * There are definitely other properties that an AttackModifier could have,
 * such as how the AttackModifier modifies an attack, but the battle
 * animation doesn't care about those properties.
 */
public interface AttackModifier {
	/**
	 * Returns the name to display when this skill is activated.
	 * If Optional.empty(), this does not display a name.
	 */
	public Optional<String> getDisplayName();
}
