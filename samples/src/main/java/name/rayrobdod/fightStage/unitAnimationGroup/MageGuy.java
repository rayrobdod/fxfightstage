package name.rayrobdod.fightStage.unitAnimationGroup;

import java.util.Set;
import java.util.function.Function;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattleAnimation.AttackModifier;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.UnitAnimationGroup;

public final class MageGuy implements UnitAnimationGroup {
	
	private static final String filename = "/name/rayrobdod/fightStage/unitAnimationGroup/mageguy.png";
	
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
		this.node.setX(-80);
		this.node.setY(-150);
		this.node.setViewport(standingViewport);
	}
	
	/**
	 * Returns the node associated with this object.
	 */
	public Node getNode() { return this.node; }
	
	public Point2D getSpellTarget() { return new Point2D(-5, -60); }
	private static final Point2D spellOrigin = new Point2D(-55, -61);
	
	@Override
	public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Point2D target
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
		
		final Animation spellAnimation = spellAnimationFun.apply(spellOrigin);
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
