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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattleAnimation.AttackModifier;
import name.rayrobdod.fightStage.BattleAnimation.Side;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.UnitAnimationGroup;

public final class SwordGuy implements UnitAnimationGroup {
	
	private static final double swordAngleSheath = 40;
	private static final double swordAngleMidSheath = 40;
	private static final double swordAngleStand = 220;
	private static final double swordAnglePose = 270;
	private static final double swordAngleRaise = 240;
	private static final double swordAngleLower = 140;
	
	private static final double swordXSheath = 110 - 120;
	private static final double swordXMidSheath = 95 - 120;
	private static final double swordXStand = 110 - 120;
	private static final double swordXPose = 80 - 120;
	private static final double swordXRaise = 85 - 120;
	private static final double swordXLower = 85 - 120;
	private static final double midSwingXDelta = -15;
	
	private static final double swordYSheath = 120 - 150;
	private static final double swordYMidSheath = 105 - 150;
	private static final double swordYStand = 120 - 150;
	private static final double swordYPose = 80 - 150;
	private static final double swordYRaise = 50 - 150;
	private static final double swordYLower = 110 - 150;
	
	private static final double swordLength = 40;
	private static final double approachToDistance = 100;
	
	private final Group node;
	private final DoubleProperty swordAngle;
	private final DoubleProperty swordHandX;
	private final DoubleProperty swordHandY;
	private final DoubleProperty facingScaleX;
	private final DoubleProperty approachX;
	private final DoubleProperty approachY;
	
	public SwordGuy() {
		// `bounds` prevents the group from changing size despite other components
		// moving around by being larger than the all other nodes combined.
		final Rectangle bounds = new Rectangle(0 - 120, 0 - 150, 150, 150);
		bounds.setFill(Color.TRANSPARENT);
		
		final Rectangle body = new Rectangle(100 - 120, 80 - 150, 40, 70);
		body.setFill(Color.RED);
		final Circle head = new Circle();
		head.setCenterX(120 - 120);
		head.setCenterY(60 - 150);
		head.setRadius(30);
		head.setFill(Color.PEACHPUFF);
		final Circle eye = new Circle();
		eye.setCenterX(106 - 120);
		eye.setCenterY(55 - 150);
		eye.setRadius(6);
		eye.setFill(Color.BLACK);
		final Circle hand = new Circle();
		hand.setRadius(8);
		hand.setFill(Color.PEACHPUFF);
		final Rectangle sword = new Rectangle();
		sword.setWidth(swordLength);
		sword.setHeight(6);
		sword.setFill(Color.SILVER);
		
		
		final Rotate swordRotate = new Rotate();
		this.swordAngle = swordRotate.angleProperty();
		this.swordHandX = swordRotate.pivotXProperty();
		this.swordHandY = swordRotate.pivotYProperty();
		final Translate approachTranslate = new Translate();
		this.approachX = approachTranslate.xProperty();
		this.approachY = approachTranslate.yProperty();
		final Scale facingScale = new Scale();
		this.facingScaleX = facingScale.xProperty();
		
		sword.xProperty().bind(this.swordHandX);
		sword.yProperty().bind(this.swordHandY.subtract(3));
		hand.centerXProperty().bind(this.swordHandX);
		hand.centerYProperty().bind(this.swordHandY);
		sword.getTransforms().add(swordRotate);
		
		this.swordAngle.set(swordAngleSheath);
		this.swordHandX.set(swordXSheath);
		this.swordHandY.set(swordYSheath);
		
		this.node = new Group(
			  bounds
			, body
			, head
			, eye
			, hand
			, sword
		);
		this.node.getTransforms().add(approachTranslate);
		this.node.getTransforms().add(facingScale);
	}
	
	@Override
	public Node getNode() { return this.node; }
	
	@Override
	public Point2D getSpellTarget(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(approachX) - rolloverKeyValues.get(facingScaleX) * 5,
			rolloverKeyValues.get(approachY) - 60
		);
	}
	
	@Override
	public double getCurrentXOffset(Map<DoubleProperty, Double> rolloverKeyValues) {
		return rolloverKeyValues.get(approachX);
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
		final Timeline approachAnimation = new Timeline();
		final Timeline beforeSpellAnimation = new Timeline();
		final Timeline afterSpellAnimation = new Timeline();
		Duration thisTime = Duration.ZERO;
		
		final boolean isFirst = consecutiveAttackDesc.isFirst();
		final boolean isEven = consecutiveAttackDesc.current % 2 == 0;
		final boolean isOdd = consecutiveAttackDesc.current % 2 != 0;
		
		if (isFirst) {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleStand, swordXStand, swordYStand)
			);
		} else if (isEven) {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleLower, swordXLower, swordYLower)
			);
		} else {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleRaise, swordXRaise, swordYRaise)
			);
		}

		final double startCurrentXOffset = this.getCurrentXOffset(rolloverKeyValues);
		final double startingVector = target.getX() - startCurrentXOffset;
		final double startingDistance = Math.abs(startingVector);
		final double approachDistance = Math.max(0, startingDistance - approachToDistance);
		final double approachVector = Math.signum(startingVector) * approachDistance;
		final double endCurrentXOffset = startCurrentXOffset + approachVector;
		
		if (approachDistance > 0) {
			final Duration approachDuration = Duration.millis(approachDistance * 5);
			
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.approachX, startCurrentXOffset, Interpolator.LINEAR)
			));
			thisTime = thisTime.add(approachDuration);
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.approachX, endCurrentXOffset, Interpolator.LINEAR)
			));
		}
		rolloverKeyValues.put(approachX, endCurrentXOffset);
		
		if (isFirst) {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleStand, swordXStand, swordYStand)
			);
		} else if (isEven) {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleLower, swordXLower, swordYLower)
			);
		} else {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleRaise, swordXRaise, swordYRaise)
			);
		}
		
		if (isFinisher) {
			thisTime = thisTime.add(Duration.millis(200));
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAnglePose, swordXPose, swordYPose)
			);
			thisTime = thisTime.add(Duration.millis(400));
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAnglePose, swordXPose, swordYPose)
			);
		}
		
		if (isFinisher || isFirst) {
			thisTime = thisTime.add(Duration.millis(200));
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleRaise, swordXRaise, swordYRaise)
			);
		}
		
		beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
			soundEffectEventHandler("name/rayrobdod/fightStage/sounds/swing.wav")
		));
		thisTime = thisTime.add(Duration.millis(100));
		beforeSpellAnimation.getKeyFrames().add(
			swordKeyFrame(thisTime,
				(swordAngleRaise + swordAngleLower) / 2,
				(swordXRaise + swordXLower) / 2 + midSwingXDelta,
				(swordYRaise + swordYLower) / 2
			)
		);
		thisTime = thisTime.add(Duration.millis(100));
		
		if (isFinisher || isFirst || isOdd) {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleLower, swordXLower, swordYLower)
			);
		} else {
			beforeSpellAnimation.getKeyFrames().add(
				swordKeyFrame(thisTime, swordAngleRaise, swordXRaise, swordYRaise)
			);
		}
		
		thisTime = Duration.ZERO;
		if (consecutiveAttackDesc.isLast()) {
			if (isFinisher || isFirst || isOdd) {
				afterSpellAnimation.getKeyFrames().add(
					swordKeyFrame(Duration.ZERO, swordAngleLower, swordXLower, swordYLower)
				);
			} else {
				afterSpellAnimation.getKeyFrames().add(
					swordKeyFrame(Duration.ZERO, swordAngleRaise, swordXRaise, swordYRaise)
				);
			}
			
			afterSpellAnimation.getKeyFrames().add(
				swordKeyFrame(Duration.millis(200), swordAngleStand, swordXStand, swordYStand)
			);
			thisTime = thisTime.add(Duration.millis(200));
		}
		
		final double facingScaleXValue = rolloverKeyValues.get(facingScaleX);
		final Point2D spellOriginRelative = (isFinisher || isFirst || isOdd
			? new Point2D(facingScaleXValue * (swordXLower + Math.cos(swordAngleLower * Math.PI / 180) * swordLength), swordYLower + Math.sin(swordAngleLower * Math.PI / 180) * swordLength)
			: new Point2D(facingScaleXValue * (swordXRaise + Math.cos(swordAngleRaise * Math.PI / 180) * swordLength), swordYRaise + Math.sin(swordAngleRaise * Math.PI / 180) * swordLength)
		);
		final Point2D currentOffset = new Point2D(
			rolloverKeyValues.get(approachX),
			rolloverKeyValues.get(approachY)
		);
		final Point2D spellOrigin = spellOriginRelative.add(currentOffset);
		
		return new SequentialTransition(
			beforeSpellAnimation,
			spellAnimationFun.apply(spellOrigin),
			afterSpellAnimation
		);
	}
	
	@Override
	public Animation getInitiateAnimation() {
		final Timeline anim = new Timeline();
		
		anim.getKeyFrames().add(swordKeyFrame(Duration.ZERO, swordAngleSheath, swordXSheath, swordYSheath));
		anim.getKeyFrames().add(swordKeyFrame(Duration.millis(150), swordAngleMidSheath, swordXMidSheath, swordYMidSheath));
		anim.getKeyFrames().add(swordKeyFrame(Duration.millis(350), swordAngleStand, swordXStand, swordYStand));
		
		return anim;
	}
	
	@Override
	public Animation getVictoryAnimation() {
		final Timeline anim = new Timeline();
		
		anim.getKeyFrames().add(swordKeyFrame(Duration.ZERO, swordAngleStand, swordXStand, swordYStand));
		anim.getKeyFrames().add(swordKeyFrame(Duration.millis(200), swordAngleMidSheath, swordXMidSheath, swordYMidSheath));
		anim.getKeyFrames().add(swordKeyFrame(Duration.millis(350), swordAngleSheath, swordXSheath, swordYSheath));
		
		return anim;
	}
	
	@Override public Map<DoubleProperty, Double> getInitializingKeyValues(
		  Side side
		, Point2D footPoint
	) {
		final Map<DoubleProperty, Double> retval = new java.util.HashMap<>();
		retval.put(facingScaleX, (side == Side.LEFT ? -1.0 : 1.0));
		retval.put(approachX, footPoint.getX());
		retval.put(approachY, footPoint.getY());
		return retval;
	}
	
	/**
	 * Returns an EventHandler which plays the specified sound upon being invoked.
	 * @param filename the url of the sound file. Nullable.
	 * @return an event handler which plays the sound effect
	 */
	private static EventHandler<ActionEvent> soundEffectEventHandler(String filename) {
		if (null == filename) {
			return null;
		} else {
			final java.net.URL fileurl = SwordGuy.class.getClassLoader().getResource(filename);
			if (null == fileurl) {
				System.out.println("Resource not found: " + filename);
				return null;
			} else {
				final AudioClip clip = new AudioClip(fileurl.toString());
				final EventHandler<ActionEvent> handler = (x -> clip.play());
				return handler;
			}
		}
	}
	
	private KeyFrame swordKeyFrame (
		  Duration frameTime
		, double swordAngle
		, double swordX
		, double swordY
	) {
		return new KeyFrame(frameTime,
			new KeyValue(this.swordAngle, swordAngle, Interpolator.LINEAR),
			new KeyValue(this.swordHandX, swordX, Interpolator.LINEAR),
			new KeyValue(this.swordHandY, swordY, Interpolator.LINEAR)
		);
	}
	
}
