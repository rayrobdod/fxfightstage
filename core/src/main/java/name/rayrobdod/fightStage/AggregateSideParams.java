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
