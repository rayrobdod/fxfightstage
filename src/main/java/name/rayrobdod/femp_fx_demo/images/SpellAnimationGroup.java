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
	 * @param hpAndShakeAnimation the effects that happen to indicate a hit. This animation will be invoked exactly once in the returned animation.
	 */
	public Animation getAnimation(Animation hpAndShakeAnimation);
	
	/**
	 * Set the 'center' point of the spell animation.
	 */
	public void relocate(double newX, double newY);
	
	public default void relocate(Point2D newPoint) {
		this.relocate(newPoint.getX(), newPoint.getY());
	}
	
}
