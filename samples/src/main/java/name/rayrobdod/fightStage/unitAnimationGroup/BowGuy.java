package name.rayrobdod.fightStage.unitAnimationGroup;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.Side;
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
	private final DoubleProperty scaleXProp;
	private final DoubleProperty translateXProp;
	private final DoubleProperty translateYProp;
	
	public BowGuy() {
		final Translate footPointTranslate = new Translate(-70, -130);
		final Scale scale = new Scale();
		this.scaleXProp = scale.xProperty();
		final Translate moveTranslate = new Translate();
		this.translateXProp = moveTranslate.xProperty();
		this.translateYProp = moveTranslate.yProperty();
		final Image img = new Image(filename);
		this.node = new ImageView(img);
		this.node.getTransforms().add(moveTranslate);
		this.node.getTransforms().add(scale);
		this.node.getTransforms().add(footPointTranslate);
		this.node.setViewport(standingViewport);
	}
	
	@Override
	public Node getNode() { return this.node; }
	
	@Override
	public Point2D getSpellTarget(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(translateXProp) - rolloverKeyValues.get(scaleXProp) * 5,
			rolloverKeyValues.get(translateYProp) - 60
		);
	}
	
	@Override
	public double getCurrentXOffset(Map<DoubleProperty, Double> rolloverKeyValues) {
		return rolloverKeyValues.get(translateXProp);
	}

	private Point2D getSpellOrigin(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(translateXProp) - rolloverKeyValues.get(scaleXProp) * 65,
			rolloverKeyValues.get(translateYProp) - 60
		);
	}
	
	@Override
	public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Map<DoubleProperty, Double> rolloverKeyValues
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
		
		return new SequentialTransition(
			beforeSpellAnimation,
			spellAnimationFun.apply(this.getSpellOrigin(rolloverKeyValues)),
			afterSpellAnimation
		);
	}
	
	@Override public Map<DoubleProperty, Double> getInitializingKeyValues(
		  Side side
		, Point2D footPoint
	) {
		final Map<DoubleProperty, Double> retval = new java.util.HashMap<>();
		retval.put(scaleXProp, (side == Side.LEFT ? -1.0 : 1.0));
		retval.put(translateXProp, footPoint.getX());
		retval.put(translateYProp, footPoint.getY());
		return retval;
	}
}
