package name.rayrobdod.fightStage;

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * An object that describes a spell animation.
 * 
 * The coordinate system should match BattleAnimation's coordinate system,
 * because everything should be done relative to the `origin` and `target` coordinates,
 * and those will be given in the BattleAnimation's coordinate system.
 */
public interface SpellAnimationGroup {
	
	/**
	 * A Node that contains every visual element that will appear behinds
	 * the characters from the perspective of the player.
	 * 
	 * For any given instance of SpellAnimationGroup, this method must return
	 * the same object upon each invocation. Different instances of
	 * SpellAnimationGroup, this function must return different objects.
	 */
	public Node getBackground();
	
	/**
	 * A Node that contains every visual element that will appear between
	 * the characters and the player
	 * 
	 * For any given instance of SpellAnimationGroup, this method must return
	 * the same object upon each invocation. Different instances of
	 * SpellAnimationGroup, this function must return different objects.
	 */
	public Node getForeground();
	
	/**
	 * Returns an animation to be used for an attack animation
	 * 
	 * The returned Animation must include panAnimation exactly once,
	 * and must include hpAndShakeAnimation exactly once.
	 * 
	 * @param origin the origin point of the spell animation
	 * @param target the target point of the spell animation
	 * @param panAnimation the effects that move the camera from the attacker to the target.
	 * @param hpAndShakeAnimation the effects that happen to indicate a hit.
	 */
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	);
	
}
