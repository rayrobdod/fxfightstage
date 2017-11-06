package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.SimpleDoubleTransition;
import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;

/** A "spell" animation for physical hits */
public final class PhysicalHit implements SpellAnimationGroup {
	
	private static final double defaultCenterX = 200;
	private static final double defaultCenterY = 200;
	private static final double radiusX = 10;
	private static final double radiusY = 30;
	private static final double flareRadius = 50;
	private static final double emanationDistance = 160;
	private static final Duration animDuration = Duration.millis(400);
	private static final Color emanationColor = Color.hsb(220, 0.8, 0.7);
	
	
	private final Ellipse[] emanations;
	private final Circle flare;
	private final Node group;
	private final DoubleProperty centerX;
	private final DoubleProperty centerY;
	
	public PhysicalHit() {
		this.centerX = new SimpleDoubleProperty(defaultCenterX);
		this.centerY = new SimpleDoubleProperty(defaultCenterY);
		
		this.emanations = new Ellipse[4];
		for (int i = 0; i < emanations.length; i++) {
			final Ellipse emanation = new Ellipse(defaultCenterX, defaultCenterY, radiusX, radiusY);
			emanation.setFill(Color.TRANSPARENT);
			emanation.setRotate(45 + i * 90);
			emanations[i] = emanation;
		}
		this.flare = new Circle(0, 0, flareRadius, Color.TRANSPARENT);
		this.flare.setStroke(Color.TRANSPARENT);
		this.flare.centerXProperty().bind(this.centerX);
		this.flare.centerYProperty().bind(this.centerY);
		
		this.group = new Group(flare, new Group(emanations));
	}
	
	public Node getNode() { return this.group; }
	
	public void setTarget(double newX, double newY) {
		this.centerX.unbind();
		this.centerY.unbind();
		this.centerX.set(newX);
		this.centerY.set(newY);
	}
	
	public void setOrigin(double newX, double newY) {
	}
	
	public Animation getAnimation(Animation hpAndShakeAnimation) {
		return new ParallelTransition(
			  new SimpleDoubleTransition(animDuration, emanations[0].centerXProperty(), centerX.get(), centerX.get() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[1].centerXProperty(), centerX.get(), centerX.get() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[2].centerXProperty(), centerX.get(), centerX.get() - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[3].centerXProperty(), centerX.get(), centerX.get() - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[0].centerYProperty(), centerY.get(), centerY.get() - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[1].centerYProperty(), centerY.get(), centerY.get() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[2].centerYProperty(), centerY.get(), centerY.get() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[3].centerYProperty(), centerY.get(), centerY.get() - emanationDistance)
			, new FillTransition(animDuration, emanations[0], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[1], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[2], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[3], emanationColor, Color.TRANSPARENT)
			, new FlarePaintTransition(animDuration.divide(3))
			, hpAndShakeAnimation
		);
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
			flare.setFill( flareGradient(newValue, centerX.get(), centerY.get()) );
		}
	}
	
	private static Paint flareGradient(Color myColor, double centerX, double centerY) {
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
