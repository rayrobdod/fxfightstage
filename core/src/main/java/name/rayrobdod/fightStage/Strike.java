package name.rayrobdod.fightStage;

import java.util.Set;

/** A description of one attack */
public final class Strike {
	/** Which unit is performing an attack */
	public final Side attacker;
	/** The damage dealt to the defender */
	public final int damage;
	/**
	 * The damage healed by the attacker.
	 * Probably can be negative for counter-attack damage.
	 */
	public final int drain;
	/** Skills triggered during this attack. Just for the sake of identity. */
	public final Set<AttackModifier> triggeredSkills;
	
	public Strike(
		  Side attacker
		, int damage
		, int drain
		, Set<AttackModifier> triggeredSkills
	) {
		this.attacker = attacker;
		this.damage = damage;
		this.drain = drain;
		this.triggeredSkills = triggeredSkills;
	}
}
