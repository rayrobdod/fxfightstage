package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.SimpleBooleanTransition;
import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;

/**
 * An arrow
 * 
 * This one in particular doesn't support changing the origin and target after
 * creating an animation.
 */
public final class Arrow implements SpellAnimationGroup {
	
	private static final int shaftWidth = 6;
	private static final int shaftLength = 35;
	private static final int headWidth = 15;
	private static final int headLength = 10;
	private static final int featherWidth = 6;
	private static final int featherLength = 18;
	private static final int featherSkew = 5;
	private static final double arrowSpeed = 1.4;
	private static final double arrowArcMultiplier = 0.08;
	
	private final Group arrow;
	private final Group node;
	private double originX, originY;
	private double targetX, targetY;
	private final PhysicalHit physicalHit;
	
	public Arrow() {
		final Shape head = new Polygon(
			0, 0,
			headLength, headWidth / 2,
			headLength, -headWidth / 2
		);
		head.setFill(Color.GREY);
		head.setStroke(Color.TRANSPARENT);
		
		final Shape shaft = new Rectangle(
			headLength - 2, -shaftWidth / 2,
			shaftLength + 2, shaftWidth
		);
		shaft.setFill(Color.BLUE);
		shaft.setStroke(Color.TRANSPARENT);
		
		final Shape feather = new Polygon(
			headLength + shaftLength - featherLength, shaftWidth / 2,
			headLength + shaftLength - featherLength + featherSkew, shaftWidth / 2 + featherWidth,
			headLength + shaftLength + featherSkew, shaftWidth / 2 + featherWidth,
			headLength + shaftLength, shaftWidth / 2,
			
			headLength + shaftLength, -shaftWidth / 2,
			headLength + shaftLength + featherSkew, -shaftWidth / 2 - featherWidth,
			headLength + shaftLength - featherLength + featherSkew, -shaftWidth / 2 - featherWidth,
			headLength + shaftLength - featherLength, -shaftWidth / 2
		);
		feather.setFill(Color.LIGHTGREY);
		feather.setStroke(Color.TRANSPARENT);
		
		this.arrow = new Group(feather, shaft, head);
		this.arrow.setScaleX(-1);
		this.arrow.setVisible(false);
		
		this.physicalHit = new PhysicalHit();
		
		this.node = new Group(arrow, physicalHit.getNode());
	}
	
	public Node getNode() { return this.node; }
	
	public void setOrigin(double newX, double newY) {
		this.originX = newX;
		this.originY = newY;
		this.physicalHit.setOrigin(newX, newY);
	}
	
	public void setTarget(double newX, double newY) {
		this.targetX = newX;
		this.targetY = newY;
		this.physicalHit.setTarget(newX, newY);
	}
	
	public Animation getAnimation(Animation hpAndShakeAnimation) {
		final double deltaX = this.targetX - this.originX;
		final double deltaY = this.targetY - this.originY;
		final double deltaDistance = Math.sqrt(
			Math.abs(targetX + targetX) * Math.abs(targetY + targetY)
		);
		final double controlX = this.originX + deltaX / 2;
		final double controlY = this.originY + deltaY / 2 - Math.abs(deltaX) * arrowArcMultiplier;
		final Duration duration = Duration.millis(deltaDistance / arrowSpeed);
		
		Shape arrowPath = new QuadCurve(
			this.originX, this.originY,
			controlX, controlY,
			this.targetX, this.targetY
		);
		
		final SimpleBooleanTransition setArrowVisible = new SimpleBooleanTransition(
			Duration.ONE, this.arrow.visibleProperty(), false, true
		);
		final PathTransition arrowAnimation = new PathTransition(
			duration, arrowPath, this.arrow
		);
		final SimpleBooleanTransition setArrowInvisible = new SimpleBooleanTransition(
			Duration.ONE, this.arrow.visibleProperty(), true, false
		);
		arrowAnimation.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		arrowAnimation.setInterpolator(Interpolator.LINEAR);
		
		
		return new SequentialTransition(
			setArrowVisible,
			arrowAnimation,
			setArrowInvisible,
			physicalHit.getAnimation(hpAndShakeAnimation)
		);
	}
}
