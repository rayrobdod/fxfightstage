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
	 * Set the target point of the spell animation.
	 */
	public void setTarget(double newX, double newY);
	
	public default void setTarget(Point2D newPoint) {
		this.setTarget(newPoint.getX(), newPoint.getY());
	}
	
	/**
	 * Set the origin point of the spell animation.
	 */
	public void setOrigin(double newX, double newY);
	
	public default void setOrigin(Point2D newPoint) {
		this.setOrigin(newPoint.getX(), newPoint.getY());
	}
	
}
