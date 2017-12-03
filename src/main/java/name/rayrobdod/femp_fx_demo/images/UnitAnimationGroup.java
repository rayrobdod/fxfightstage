package name.rayrobdod.femp_fx_demo.images;

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import name.rayrobdod.femp_fx_demo.ConsecutiveAttackDescriptor;

public interface UnitAnimationGroup {
	
	/**
	 * Returns the node associated with this object.
	 * The object returned has the same identity every time. 
	 */
	public Node getNode();
	
	/**
	 * Returns a point relative to the node at which the standing pose of the unit is centered.
	 */
	public Point2D getFootPoint();
	
	/**
	 * Returns the location on the node at which spells targeting this unit should be centered.
	 */
	public Point2D getSpellTarget();
	
	/**
	 * Returns the location on the node at which spells cast by this unit should originate.
	 */
	public Point2D getSpellOrigin();
	
	/**
	 * Returns an animation to be used for an attack animation
	 * @param spellAnimation the hit animation associated with the weapon.  This animation will be invoked exactly once in the returned animation.
	 */
	public Animation getAttackAnimation(
		  Animation spellAnimation
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
	);
	
}
