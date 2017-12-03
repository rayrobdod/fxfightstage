package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.ConsecutiveAttackDescriptor;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;

public final class BowGuy implements UnitAnimationGroup {
	
	private static final String filename = "/name/rayrobdod/femp_fx_demo/images/battle_unit_anim/bowguy.png";
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,100,130);
	private static final Duration frameLength = Duration.seconds(1.0 / 8.0);
	private static final Rectangle2D[] beforeSpellViewports = {
		standingViewport,
		new Rectangle2D(100,0,100,130),
		new Rectangle2D(200,0,100,130),
		new Rectangle2D(300,0,100,130),
		new Rectangle2D(400,0,100,130)
	};
	private static final Rectangle2D[] beforeConsecutiveSpellViewports = {
		new Rectangle2D(400,0,100,130),
		new Rectangle2D(600,0,100,130),
		new Rectangle2D(200,0,100,130),
		new Rectangle2D(300,0,100,130),
		new Rectangle2D(400,0,100,130)
	};
	private static final Rectangle2D[] afterSpellViewports = {
		new Rectangle2D(400,0,100,130),
		new Rectangle2D(500,0,100,130),
		standingViewport
	};
	
	
	private final ImageView node;
	
	public BowGuy() {
		final Image img = new Image(filename);
		this.node = new ImageView(img);
		this.node.setViewport(standingViewport);
	}
	
	/**
	 * Returns the node associated with this object.
	 */
	public Node getNode() { return this.node; }
	
	public Point2D getFootPoint() { return new Point2D(70, 130); }
	public Point2D getSpellTarget() { return new Point2D(65, 70); }
	public Point2D getSpellOrigin() { return new Point2D(5, 70); }
	
	/**
	 * Returns an animation to be used for an attack animation
	 */
	public Animation getAttackAnimation(
		  Animation hitAnimation
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
	) {
		final Timeline beforeSpellAnimation = new Timeline();
		if (consecutiveAttackDesc.isFirst()) {
			for (int i = 0; i < beforeSpellViewports.length; i++) {
				final Duration thisTime = frameLength.multiply(i);
				beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
					new KeyValue(node.viewportProperty(), beforeSpellViewports[i], Interpolator.DISCRETE)
				));
			}
		} else {
			for (int i = 0; i < beforeConsecutiveSpellViewports.length; i++) {
				final Duration thisTime = frameLength.multiply(i);
				beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
					new KeyValue(node.viewportProperty(), beforeConsecutiveSpellViewports[i], Interpolator.DISCRETE)
				));
			}
		}
		
		final Timeline afterSpellAnimation = new Timeline();
		if (consecutiveAttackDesc.isLast()) {
			for (int i = 0; i < afterSpellViewports.length; i++) {
				final Duration thisTime = frameLength.multiply(i);
				afterSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
					new KeyValue(node.viewportProperty(), afterSpellViewports[i], Interpolator.DISCRETE)
				));
			}
		}
		
		return new SequentialTransition(
			beforeSpellAnimation,
			hitAnimation,
			afterSpellAnimation
		);
	}
	
}
