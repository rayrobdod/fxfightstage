package name.rayrobdod.femp_fx_demo.images;

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public interface SpellAnimationGroup {
	
	/**
	 * A Node that contains every visual element that will appear behinds
	 * the characters from the perspective of the player
	 */
	public Node getBackground();
	/**
	 * A Node that contains every visual element that will appear between
	 * the characters and the player
	 */
	public Node getForeground();
	
	/**
	 * Returns an animation to be used for an attack animation
	 * @param origin the origin point of the spell animation
	 * @param target the target point of the spell animation
	 * @param panAnimation the effects that move the camera from the attacker to the target. This animation will be invoked exactly once in the returned animation.
	 * @param hpAndShakeAnimation the effects that happen to indicate a hit. This animation will be invoked exactly once in the returned animation.
	 */
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	);
	
}
