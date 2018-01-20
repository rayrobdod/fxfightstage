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
	/** Skills triggered by the attacker during this strike */
	public final Set<AttackModifier> attackerModifiers;
	/** Skills triggered by the defender during this strike */
	public final Set<AttackModifier> defenderModifiers;
	
	public Strike(
		  Side attacker
		, int damage
		, int drain
		, Set<AttackModifier> attackerModifiers
		, Set<AttackModifier> defenderModifiers
	) {
		this.attacker = attacker;
		this.damage = damage;
		this.drain = drain;
		this.attackerModifiers = attackerModifiers;
		this.defenderModifiers = defenderModifiers;
	}
	
	public int maxModifierSize() {
		return Math.max(attackerModifiers.size(), defenderModifiers.size());
	}
}
