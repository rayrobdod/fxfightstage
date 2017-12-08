package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import java.util.Set;

import javafx.animation.*;
import javafx.animation.Animation;
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
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.BattleAnimation.AttackModifier;
import name.rayrobdod.femp_fx_demo.ConsecutiveAttackDescriptor;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;

public final class SwordGuy implements UnitAnimationGroup {
	
	private static final double swordAngleSheath = 40;
	private static final double swordAngleStand = 220;
	private static final double swordAnglePose = 270;
	private static final double swordAngleRaise = 240;
	private static final double swordAngleLower = 140;
	
	private static final double swordXSheath = 110;
	private static final double swordXStand = 110;
	private static final double swordXPose = 80;
	private static final double swordXRaise = 85;
	private static final double swordXLower = 85;
	private static final double midSwingXDelta = -15;
	
	private static final double swordYSheath = 120;
	private static final double swordYStand = 120;
	private static final double swordYPose = 80;
	private static final double swordYRaise = 50;
	private static final double swordYLower = 110;
	
	
	private final Group node;
	private final DoubleProperty swordAngle;
	private final DoubleProperty swordHandX;
	private final DoubleProperty swordHandY;
	
	public SwordGuy() {
		final Rectangle bounds = new Rectangle(0, 0, 150, 150);
		bounds.setFill(Color.TRANSPARENT);
		final Rectangle body = new Rectangle(100, 80, 40, 70);
		body.setFill(Color.RED);
		final Circle head = new Circle();
		head.setCenterX(120);
		head.setCenterY(60);
		head.setRadius(30);
		head.setFill(Color.PEACHPUFF);
		final Circle eye = new Circle();
		eye.setCenterX(106);
		eye.setCenterY(55);
		eye.setRadius(6);
		eye.setFill(Color.BLACK);
		final Circle hand = new Circle();
		hand.setRadius(8);
		hand.setFill(Color.PEACHPUFF);
		final Rectangle sword = new Rectangle();
		sword.setWidth(40);
		sword.setHeight(6);
		sword.setFill(Color.SILVER);
		
		
		final Rotate swordRotate = new Rotate();
		this.swordAngle = swordRotate.angleProperty();
		this.swordHandX = swordRotate.pivotXProperty();
		this.swordHandY = swordRotate.pivotYProperty();
		
		sword.xProperty().bind(this.swordHandX);
		sword.yProperty().bind(this.swordHandY.subtract(3));
		hand.centerXProperty().bind(this.swordHandX);
		hand.centerYProperty().bind(this.swordHandY);
		sword.getTransforms().add(swordRotate);
		
		this.swordAngle.set(220);
		this.swordHandX.set(110);
		this.swordHandY.set(120);
		
		this.node = new Group(
			  bounds
			, body
			, head
			, eye
			, hand
			, sword
		);
	}
	
	/**
	 * Returns the node associated with this object.
	 */
	public Node getNode() { return this.node; }
	
	public Point2D getFootPoint() { return new Point2D(120, 150); }
	public Point2D getSpellTarget() { return new Point2D(115, 90); }
	public Point2D getSpellOrigin() { return new Point2D(20, 100); }
	
	/**
	 * Returns an animation to be used for an attack animation
	 */
	public Animation getAttackAnimation(
		  Animation hitAnimation
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> triggeredSkills
		, boolean isFinisher
	) {
		final Timeline beforeSpellAnimation = new Timeline();
		final Timeline afterSpellAnimation = new Timeline();
		Duration thisTime = Duration.ZERO;
		
		final boolean isFirst = consecutiveAttackDesc.isFirst();
		final boolean isEven = consecutiveAttackDesc.current % 2 == 0;
		final boolean isOdd = consecutiveAttackDesc.current % 2 != 0;
		
		if (isFirst) {
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAngleStand, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXStand, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYStand, Interpolator.LINEAR)
			));
		} else if (isEven) {
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAngleLower, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXLower, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYLower, Interpolator.LINEAR)
			));
		} else {
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAngleRaise, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXRaise, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYRaise, Interpolator.LINEAR)
			));
		}
		
		if (isFinisher) {
			thisTime = thisTime.add(Duration.millis(200));
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAnglePose, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXPose, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYPose, Interpolator.LINEAR)
			));
			thisTime = thisTime.add(Duration.millis(400));
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAnglePose, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXPose, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYPose, Interpolator.LINEAR)
			));
		}
		
		if (isFinisher || isFirst) {
			thisTime = thisTime.add(Duration.millis(200));
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAngleRaise, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXRaise, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYRaise, Interpolator.LINEAR)
			));
		}
		
		beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
			soundEffectEventHandler("name/rayrobdod/femp_fx_demo/sounds/swing.wav")
		));
		thisTime = thisTime.add(Duration.millis(100));
		beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
			new KeyValue(this.swordAngle, (swordAngleRaise + swordAngleLower) / 2, Interpolator.LINEAR),
			new KeyValue(this.swordHandX, (swordXRaise + swordXLower) / 2 + midSwingXDelta, Interpolator.LINEAR),
			new KeyValue(this.swordHandY, (swordYRaise + swordYLower) / 2, Interpolator.LINEAR)
		));
		thisTime = thisTime.add(Duration.millis(100));
		
		if (isFinisher || isFirst || isOdd) {
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAngleLower, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXLower, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYLower, Interpolator.LINEAR)
			));
		} else {
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(this.swordAngle, swordAngleRaise, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXRaise, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYRaise, Interpolator.LINEAR)
			));
		}
		
		
		if (consecutiveAttackDesc.isLast()) {
			if (isFinisher || isFirst || isOdd) {
				afterSpellAnimation.getKeyFrames().add(new KeyFrame(Duration.ZERO,
					new KeyValue(this.swordAngle, swordAngleLower, Interpolator.LINEAR),
					new KeyValue(this.swordHandX, swordXLower, Interpolator.LINEAR),
					new KeyValue(this.swordHandY, swordYLower, Interpolator.LINEAR)
				));
			} else {
				afterSpellAnimation.getKeyFrames().add(new KeyFrame(Duration.ZERO,
					new KeyValue(this.swordAngle, swordAngleRaise, Interpolator.LINEAR),
					new KeyValue(this.swordHandX, swordXRaise, Interpolator.LINEAR),
					new KeyValue(this.swordHandY, swordYRaise, Interpolator.LINEAR)
				));
			}
			
			afterSpellAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200),
				new KeyValue(this.swordAngle, swordAngleStand, Interpolator.LINEAR),
				new KeyValue(this.swordHandX, swordXStand, Interpolator.LINEAR),
				new KeyValue(this.swordHandY, swordYStand, Interpolator.LINEAR)
			));
		}
		
		
		return new SequentialTransition(
			beforeSpellAnimation,
			hitAnimation,
			afterSpellAnimation
		);
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
	
}
