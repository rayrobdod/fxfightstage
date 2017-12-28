package name.rayrobdod.fightStage;

import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattleAnimation.AttackModifier;

/**
 * An object that describes a unit animation.
 * 
 * The coordinate system should have the same scale as BattleAnimation, but 0,0
 * is located at the unit's feet (if the unit is an infantry; probably standardize elsewhere
 * for mounted or flying).
 */
public interface UnitAnimationGroup {
	
	/**
	 * Returns the node associated with this object.
	 * The object returned has the same identity every time. 
	 */
	public Node getNode();
	
	/**
	 * Returns the location on the node at which spells targeting this unit should be centered.
	 */
	public Point2D getSpellTarget();
	
	/**
	 * Returns the location on the node at which spells cast by this unit should originate.
	 */
	public Point2D getSpellOrigin();
	
	/**
	 * Returns an animation used to represent an attack
	 * @param spellAnimation the hit animation associated with the weapon.  This animation must be invoked exactly once in the returned animation.
	 * @param consecutiveAttackDesc Describes this attack's position in a sequence of consecutive strikes
	 * @param triggeredSkills modifiers describing the current attack
	 * @param isFinisher true if this attack reduces the opponent's HP to zero
	 */
	public Animation getAttackAnimation(
		  Animation spellAnimation
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> triggeredSkills
		, boolean isFinisher
	);
	
	/*
	 * Returns an animation used to represent being hit by an attack
	 */
	// public Animation getHitAnimation(
	//	, Set<AttackModifier> triggeredSkills
	//	, boolean isFinisher
	//)
	
	/**
	 * Returns an animation used before any attacks are played
	 */
	default Animation getInitiateAnimation() { return new PauseTransition(Duration.ZERO); }
	
	/**
	 * Returns an animation used after all attacks if that unit reduced it's opponent's HP to zero
	 */
	default Animation getVictoryAnimation() { return new PauseTransition(Duration.ZERO); }
	
}
