package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

/** A "spell" animation for physical hits */
public final class PhysicalHit {
	
	private static final double centerX = 360;
	private static final double centerY = 200;
	private static final double radiusX = 10;
	private static final double radiusY = 30;
	private static final double flareRadius = 50;
	private static final double emanationDistance = 160;
	private static final Duration animDuration = Duration.millis(400);
	private static final Color emanationColor = Color.hsb(220, 0.8, 0.7);
	
	
	private final Ellipse[] emanations;
	private final Circle flare;
	private final Node group;
	
	public PhysicalHit(/* ?? attacker and target locations ?? */) {
		this.emanations = new Ellipse[4];
		for (int i = 0; i < emanations.length; i++) {
			final Ellipse emanation = new Ellipse(centerX, centerY, radiusX, radiusY);
			emanation.setFill(Color.TRANSPARENT);
			emanation.setRotate(45 + i * 90);
			emanations[i] = emanation;
		}
		this.flare = new Circle(centerX, centerY, flareRadius, Color.TRANSPARENT);
		this.flare.setStroke(Color.TRANSPARENT);
		
		this.group = new Group(flare, new Group(emanations));
	}
	
	public Node getNode() { return this.group; }
	
	public Animation getAnimation() {
		return new ParallelTransition(
			  new SimpleDoubleTransition(animDuration, emanations[0].centerXProperty(), centerX, centerX + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[1].centerXProperty(), centerX, centerX + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[2].centerXProperty(), centerX, centerX - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[3].centerXProperty(), centerX, centerX - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[0].centerYProperty(), centerY, centerY - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[1].centerYProperty(), centerY, centerY + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[2].centerYProperty(), centerY, centerY + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[3].centerYProperty(), centerY, centerY - emanationDistance)
			, new FillTransition(animDuration, emanations[0], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[1], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[2], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[3], emanationColor, Color.TRANSPARENT)
			, new FlarePaintTransition(animDuration.divide(3))
		);
	}
	
	
	private static final class SimpleDoubleTransition extends Transition {
		private DoubleProperty property;
		private double fromValue;
		private double toValue;
		
		/** Assumes parameters are not null */
		public SimpleDoubleTransition(Duration duration, DoubleProperty property, double fromValue, double toValue) {
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
			double newValue = this.getCachedInterpolator().interpolate(fromValue, toValue, frac);
			this.property.set(newValue);
		}
	}
	
	private final class FlarePaintTransition extends Transition {
		
		public FlarePaintTransition(Duration duration) {
			this.setCycleDuration(duration);
			this.setInterpolator(Interpolator.LINEAR);
		}
		
		@Override
		protected void interpolate(double frac) {
			if (frac < 0.0) { frac = 0.0; }
			if (frac > 1.0) { frac = 1.0; }
			double frac2 = this.getCachedInterpolator().interpolate(0.0, 1.0, frac);
			Color newValue = emanationColor.interpolate(Color.TRANSPARENT, frac2);
			flare.setFill( flareGradient(newValue) );
		}
	}
	
	private static Paint flareGradient(Color myColor) {
		Color midStop = Color.color(myColor.getRed(), myColor.getGreen(), myColor.getBlue(), myColor.getOpacity() * 0.3);
		
		return new javafx.scene.paint.RadialGradient(
			/* focusAngle */ 0,
			/* focusDistance */ 0,
			/* centerX */ centerX,
			/* centerY */ centerY,
			/* radius */ flareRadius,
			/* proportional */ false,
			javafx.scene.paint.CycleMethod.REFLECT,
			new javafx.scene.paint.Stop(0, Color.TRANSPARENT),
			new javafx.scene.paint.Stop(0.6, midStop),
			new javafx.scene.paint.Stop(0.7, myColor),
			new javafx.scene.paint.Stop(0.8, midStop),
			new javafx.scene.paint.Stop(1.0, Color.TRANSPARENT)
		);
	}
}
