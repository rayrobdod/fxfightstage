package name.rayrobdod.fightStage.spellAnimationGroup;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SimpleDoubleTransition;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/** A "spell" animation for physical hits */
public final class PhysicalHit implements SpellAnimationGroup {
	
	private static final double defaultCenterX = 200;
	private static final double defaultCenterY = 200;
	private static final double emanationRadiusX = 10;
	private static final double emanationRadiusY = 30;
	private static final double flareRadius = 50;
	private static final double emanationDistance = 160;
	private static final Duration animDuration = Duration.millis(400);
	private static final Color emanationColor = Color.hsb(220, 0.8, 0.7);
	
	
	private final Ellipse[] emanations;
	private final Circle flare;
	private final Node group;
	
	private final Circle background;
	
	public PhysicalHit() {
		this.emanations = new Ellipse[4];
		for (int i = 0; i < emanations.length; i++) {
			final Ellipse emanation = new Ellipse(defaultCenterX, defaultCenterY, emanationRadiusX, emanationRadiusY);
			emanation.setFill(Color.TRANSPARENT);
			emanation.setRotate(45 + i * 90);
			emanations[i] = emanation;
		}
		this.flare = new Circle(defaultCenterX, defaultCenterY, flareRadius, Color.TRANSPARENT);
		this.flare.setStroke(Color.TRANSPARENT);
		
		this.group = new Group(flare, new Group(emanations));
		this.background = new Circle();
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.group; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		return new ParallelTransition(
			  new SimpleDoubleTransition(animDuration, emanations[0].centerXProperty(), target.getX(), target.getX() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[1].centerXProperty(), target.getX(), target.getX() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[2].centerXProperty(), target.getX(), target.getX() - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[3].centerXProperty(), target.getX(), target.getX() - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[0].centerYProperty(), target.getY(), target.getY() - emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[1].centerYProperty(), target.getY(), target.getY() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[2].centerYProperty(), target.getY(), target.getY() + emanationDistance)
			, new SimpleDoubleTransition(animDuration, emanations[3].centerYProperty(), target.getY(), target.getY() - emanationDistance)
			, new FillTransition(animDuration, emanations[0], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[1], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[2], emanationColor, Color.TRANSPARENT)
			, new FillTransition(animDuration, emanations[3], emanationColor, Color.TRANSPARENT)
			, new FlarePaintTransition(animDuration.divide(3), target)
			, new SimpleDoubleTransition(animDuration, flare.centerXProperty(), target.getX(), target.getX())
			, new SimpleDoubleTransition(animDuration, flare.centerYProperty(), target.getY(), target.getY())
			, panAnimation
			, hpAndShakeAnimation
		);
	}
	
	private final class FlarePaintTransition extends Transition {
		
		private final Point2D target;
		
		public FlarePaintTransition(Duration duration, Point2D center) {
			this.target = center;
			this.setCycleDuration(duration);
			this.setInterpolator(Interpolator.LINEAR);
		}
		
		@Override
		protected void interpolate(double frac) {
			if (frac < 0.0) { frac = 0.0; }
			if (frac > 1.0) { frac = 1.0; }
			double frac2 = this.getCachedInterpolator().interpolate(0.0, 1.0, frac);
			Color newValue = emanationColor.interpolate(Color.TRANSPARENT, frac2);
			flare.setFill( flareGradient(newValue, target.getX(), target.getY()) );
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
