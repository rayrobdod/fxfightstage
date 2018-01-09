package name.rayrobdod.fightStage;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.util.Duration;

/**
 * A transition that changes the value of the specified property between the specified values.
 * 
 * Unlike fx's built-in Transitions, this cannot reset properties upon an animation reset or
 * do any relative operations as the necessary-to-override methods are package-private.
 * 
 * @deprecated use {@link Animations#booleanSetAnimation} instead
 */
@Deprecated
public final class SimpleBooleanTransition extends Transition {
	private final BooleanProperty property;
	private final boolean fromValue;
	private final boolean toValue;
	
	public SimpleBooleanTransition(Duration duration, BooleanProperty property, boolean fromValue, boolean toValue) {
		this.setCycleDuration(duration);
		this.setInterpolator(Interpolator.DISCRETE);
		this.property = property;
		this.fromValue = fromValue;
		this.toValue = toValue;
	}
	
	@Override
	protected void interpolate(double frac) {
		if (frac < 0.0) { frac = 0.0; }
		if (frac > 1.0) { frac = 1.0; }
		boolean newValue = this.getCachedInterpolator().interpolate(fromValue, toValue, frac);
		this.property.set(newValue);
	}
}
