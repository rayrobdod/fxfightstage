/*
 * Copyright 2018 Raymond Dodge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	/**
	 * The larger of `attackModifiers`'s size or `defenderModifiers`'s size
	 */
	public int maxModifierSize() {
		return Math.max(attackerModifiers.size(), defenderModifiers.size());
	}
}
