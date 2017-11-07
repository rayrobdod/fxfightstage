package name.rayrobdod.femp_fx_demo.images;

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public interface SpellAnimationGroup {
	
	/**
	 * Returns the node associated with this object.
	 * The object returned has the same identity every time. 
	 */
	public Node getNode();
	
	/**
	 * Returns an animation to be used for an attack animation
	 * @param origin the origin point of the spell animation
	 * @param target the target point of the spell animation
	 * @param hpAndShakeAnimation the effects that happen to indicate a hit. This animation will be invoked exactly once in the returned animation.
	 */
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation hpAndShakeAnimation
	);
	
}
