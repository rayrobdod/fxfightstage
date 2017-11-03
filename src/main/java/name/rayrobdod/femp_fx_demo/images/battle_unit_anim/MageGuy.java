package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public final class MageGuy {
	
	private static final String filename = "/name/rayrobdod/femp_fx_demo/images/battle_unit_anim/mageguy.png";
	
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,120,150);
	
	private static final Rectangle2D[] beforeSpellViewports = {
		standingViewport,
		new Rectangle2D(120,0,120,150),
		new Rectangle2D(240,0,120,150),
		new Rectangle2D(360,0,120,150)
	};
	private static final Rectangle2D[] duringSpellViewports = {
		new Rectangle2D(480,0,120,150),
		new Rectangle2D(360,0,120,150)
	};
	private static final Rectangle2D[] afterSpellViewports = {
		new Rectangle2D(240,0,120,150),
		new Rectangle2D(120,0,120,150),
		standingViewport
	};
	
	private static final Duration frameLength = Duration.seconds(1.0 / 15.0);
	
	private final ImageView node;
	
	public MageGuy() {
		final Image img = new Image(filename);
		this.node = new ImageView(img);
		this.node.setViewport(standingViewport);
	}
	
	/**
	 * Returns the node associated with this object.
	 */
	public Node getNode() { return this.node; }
	
	/**
	 * Returns an animation to be used for an attack animation
	 */
	public Animation getAttackAnimation(Animation spellAnimation) {
		final Timeline beforeSpellAnimation = new Timeline();
		for (int i = 0; i < beforeSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), beforeSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		final Timeline duringSpellAnimation = new Timeline();
		for (int i = 0; i < duringSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i + 1);
			duringSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), duringSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		final Timeline afterSpellAnimation = new Timeline();
		for (int i = 0; i < afterSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			afterSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), afterSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		
		Animation middle;
		if (null != spellAnimation) {
			// Repeat duringSpellAnimation until it has about the same duration as the spell
			duringSpellAnimation.setCycleCount((int) (
				spellAnimation.getTotalDuration().toMillis() / duringSpellAnimation.getCycleDuration().toMillis()
			));
			
			middle = new ParallelTransition(
				spellAnimation,
				duringSpellAnimation
			);
		} else {
			middle = duringSpellAnimation;
		}
		
		return new SequentialTransition(
			beforeSpellAnimation,
			middle,
			afterSpellAnimation
		);
	}
	
}
