package name.rayrobdod.fightStage;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.IntegerProperty;
import javafx.util.Duration;

/**
 * A transition that changes the value of the specified property between the specified values.
 * 
 * Unlike fx's built-in Transitions, this cannot reset properties upon an animation reset or
 * do any relative operations as the necessary-to-override methods are package-private.
 */
public final class SimpleIntegerTransition extends Transition {
	private final IntegerProperty property;
	private final int fromValue;
	private final int toValue;
	
	public SimpleIntegerTransition(Duration duration, IntegerProperty property, int fromValue, int toValue) {
		this.setCycleDuration(duration);
		this.setInterpolator(Interpolator.LINEAR);
		this.property = property;
		this.fromValue = fromValue;
		this.toValue = toValue;
	}
	
	@Override
	protected void interpolate(double frac) {
		if (frac < 0.0) { frac = 0.0; }
		if (frac > 1.0) { frac = 1.0; }
		int newValue = this.getCachedInterpolator().interpolate(fromValue, toValue, frac);
		this.property.set(newValue);
	}
}
