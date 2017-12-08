package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import java.util.Set;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.BattleAnimation.AttackModifier;
import name.rayrobdod.femp_fx_demo.ConsecutiveAttackDescriptor;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;

public final class MageGuy implements UnitAnimationGroup {
	
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
	
	public Point2D getFootPoint() { return new Point2D(80, 150); }
	public Point2D getSpellTarget() { return new Point2D(75, 90); }
	public Point2D getSpellOrigin() { return new Point2D(25, 89); }
	
	/**
	 * Returns an animation to be used for an attack animation
	 */
	public Animation getAttackAnimation(
		  Animation spellAnimation
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> triggeredSkills
		, boolean isFinisher
	) {
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
		
		duringSpellAnimation.setCycleCount((int) (
			spellAnimation.getTotalDuration().toMillis() / duringSpellAnimation.getCycleDuration().toMillis()
		));
		
		return new SequentialTransition(
			beforeSpellAnimation,
			new ParallelTransition(
				spellAnimation,
				duringSpellAnimation
			),
			afterSpellAnimation
		);
	}
	
}
