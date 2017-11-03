package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;

public final class Dark implements SpellAnimationGroup {
	
	private static final Duration fadeInTime = Duration.seconds(0.3);
	private static final Duration fadeOutTime = Duration.seconds(0.2);
	private static final Duration stayTime = Duration.seconds(0.5);
	private static final Duration endDelayTime = Duration.seconds(0.1);
	private static final Color mainColor = Color.rgb(224, 196, 255);
	private static final int mainRadius = 80;
	private static final double defaultCenterX = 200;
	private static final double defaultCenterY = 200;
	
	
	private final Circle node;
	private final DoubleProperty centerX;
	private final DoubleProperty centerY;
	
	public Dark(/* ?? attacker and target locations ?? */) {
		this.centerX = new SimpleDoubleProperty(defaultCenterX);
		this.centerY = new SimpleDoubleProperty(defaultCenterY);
		
		this.node = new Circle(0, 0, mainRadius, Color.BLACK);
		this.node.setBlendMode(javafx.scene.effect.BlendMode.EXCLUSION);
		this.node.centerXProperty().bind(this.centerX);
		this.node.centerYProperty().bind(this.centerY);
	}
	
	public Node getNode() { return this.node; }
	
	public void relocate(double newX, double newY) {
		this.centerX.unbind();
		this.centerY.unbind();
		this.centerX.set(newX);
		this.centerY.set(newY);
	}
	
	public Animation getAnimation(Animation hpAndShakeAnimation) {
		return new SequentialTransition(
			new FillTransition(fadeInTime, this.node, Color.BLACK, mainColor),
			new PauseTransition(stayTime),
			new ResizeCircleTransition(fadeOutTime, this.node, mainRadius, 0),
			new ParallelTransition(
				hpAndShakeAnimation,
				new PauseTransition(endDelayTime)
			)
		);
	}
	
	/** Assumes parameters are not null and that parameters are not updated after construction */
	private static final class ResizeCircleTransition extends Transition {
		private Circle shape;
		private double fromRadius;
		private double toRadius;
		
		/** Assumes parameters are not null */
		public ResizeCircleTransition(Duration duration, Circle shape, double fromRadius, double toRadius) {
			this.setCycleDuration(duration);
			this.shape = shape;
			this.fromRadius = fromRadius;
			this.toRadius = toRadius;
		}
		
		@Override
		protected void interpolate(double frac) {
			if (frac < 0.0) { frac = 0.0; }
			if (frac > 1.0) { frac = 1.0; }
			double newValue = this.getCachedInterpolator().interpolate(fromRadius, toRadius, frac);
			this.shape.setRadius(newValue);
		}
	}
	
}
