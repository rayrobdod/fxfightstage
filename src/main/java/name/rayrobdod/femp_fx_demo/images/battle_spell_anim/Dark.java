package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public final class Dark {
	
	private static final Duration fadeInTime = Duration.seconds(0.3);
	private static final Duration fadeOutTime = Duration.seconds(0.2);
	private static final Duration stayTime = Duration.seconds(0.5);
	private static final Duration endDelayTime = Duration.seconds(0.1);
	private static final Color mainColor = Color.rgb(224, 196, 255);
	private static final int mainRadius = 80;
	
	
	private final Circle node;
	
	public Dark(/* ?? attacker and target locations ?? */) {
		this.node = new Circle(360, 200, mainRadius, Color.BLACK);
		this.node.setBlendMode(javafx.scene.effect.BlendMode.EXCLUSION);
	}
	
	public Node getNode() { return this.node; }
	
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
