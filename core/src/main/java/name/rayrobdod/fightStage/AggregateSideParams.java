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

import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * Represents the complete set of BattleAnimation::buildAnimation
 * arguments which have values for both combatants
 */
public final class AggregateSideParams {
	public final UnitAnimationGroup unit;
	public final SpellAnimationGroup spell;
	public final Color teamColor;
	public final String unitName;
	public final String weaponName;
	public final Node weaponIcon;
	/** The unit's maximum hitpoints */
	public final int maximumHitpoints;
	/** The unit's starting current hitpoints */
	public final int initialCurrentHitpoints;
	
	public AggregateSideParams(
		  UnitAnimationGroup unit
		, SpellAnimationGroup spell
		, Color teamColor
		, String unitName
		, String weaponName
		, Node weaponIcon
		, int maximumHitpoints
		, int initialCurrentHitpoints
	){
		this.unit = unit;
		this.spell = spell;
		this.teamColor = teamColor;
		this.unitName = unitName;
		this.weaponName = weaponName;
		this.weaponIcon = weaponIcon;
		this.maximumHitpoints = maximumHitpoints;
		this.initialCurrentHitpoints = initialCurrentHitpoints;
	}
}
