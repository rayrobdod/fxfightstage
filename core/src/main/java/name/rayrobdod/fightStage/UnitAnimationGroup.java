package name.rayrobdod.fightStage;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * An object that describes a unit animation.
 * 
 * An implementor may find it easiest to add a {@link javafx.scene.transform.Scale}
 * and a {@link javafx.scene.transform.Translate} to the node's transforms, us
 * those to describe the node's offsets, and otherwise have operations take place
 * as if on a personal coordinate system.
 */
public interface UnitAnimationGroup {
	
	/**
	 * Returns the node associated with this object.
	 * The object returned has the same identity every time. 
	 */
	public Node getNode();
	
	/**
	 * Returns the location on the node at which spells targeting this unit should be centered.
	 * 
	 * @param rolloverKeyValues the return value of `getInitializingKeyValues`. Probably mutable.
	 */
	public Point2D getSpellTarget(Map<DoubleProperty, Double> rolloverKeyValues);
	
	/**
	 * Returns the offset of this unit in the x-direction.
	 * 
	 * Barring any mutations of rolloverKeyValues by {@code getAttackAnimation},
	 * this should have the same value as the x-coordinate of the point passed to {@link getInitializingKeyValues}
	 * when rolloverKeyValues was returned from that function.
	 * 
	 * @param rolloverKeyValues the return value of `getInitializingKeyValues`. Probably mutable.
	 */
	public double getCurrentXOffset(Map<DoubleProperty, Double> rolloverKeyValues);
	
	/**
	 * Returns an animation used to represent an attack
	 * @param spellAnimationFun the hit animation associated with the weapon.
			The function input is the spell origin.
			This animation must be invoked exactly once in the returned animation.
	 * @param target the target point of the spell animation
	 * @param consecutiveAttackDesc Describes this attack's position in a sequence of consecutive strikes
	 * @param triggeredSkills modifiers describing the current attack
	 * @param isFinisher true if this attack reduces the opponent's HP to zero
	 * @param rolloverKeyValues the return value of `getInitializingKeyValues`. Probably mutable.
	 */
	public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Map<DoubleProperty, Double> rolloverKeyValues
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> triggeredSkills
		, boolean isFinisher
	);
	
	/**
	 * Returns an animation used to represent being hit by an attack
	 */
	default Animation getHitAnimation(
		  Map<DoubleProperty, Double> rolloverKeyValues
		, Set<AttackModifier> triggeredSkills
		, boolean isFinisher
	) { return Animations.nil(); }
	
	/**
	 * Returns an animation used once before any attacks are played
	 */
	default Animation getInitiateAnimation() { return Animations.nil(); }
	
	/**
	 * Returns an animation used after all attacks if that unit reduced it's opponent's HP to zero
	 */
	default Animation getVictoryAnimation() { return Animations.nil(); }
	
	/**
	 * Returns a map of property-value pairs which compose the starting
	 * values of the rolloverKeyValues map
	 * 
	 * @param side The side of the battle that is unit is on.
	 * @param initialOffst The initial 'foot point' of the unit
	 * @return a map of Properties and their values
	 */
	public Map<DoubleProperty, Double> getInitializingKeyValues(
		  Side side
		, Point2D initialOffset
	);
}
