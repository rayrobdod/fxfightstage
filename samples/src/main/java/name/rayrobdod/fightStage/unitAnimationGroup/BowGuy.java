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

public final class BowGuy implements UnitAnimationGroup {
	
	private static final String filename = "/name/rayrobdod/fightStage/unitAnimationGroup/bowguy.png";
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
		this.node.setX(-70);
		this.node.setY(-130);
		this.node.setViewport(standingViewport);
	}
	
	/**
	 * Returns the node associated with this object.
	 */
	public Node getNode() { return this.node; }
	
	public Point2D getSpellTarget() { return new Point2D(-5, -60); }
	private static final Point2D spellOrigin = new Point2D(-65, -60);
	
	@Override
	public AnimationOffsetPair getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> triggeredSkills
		, boolean isFinisher
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
		
		return new AnimationOffsetPair(
			new SequentialTransition(
				beforeSpellAnimation,
				spellAnimationFun.apply(spellOrigin),
				afterSpellAnimation
			)
			, 0.0
		);
	}
	
}
