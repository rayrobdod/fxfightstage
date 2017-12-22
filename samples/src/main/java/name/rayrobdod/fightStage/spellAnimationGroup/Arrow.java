package name.rayrobdod.fightStage.spellAnimationGroup;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SimpleBooleanTransition;
import name.rayrobdod.fightStage.SpellAnimationGroup;

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
		
		this.node = new Group(arrow, physicalHit.getForeground());
	}
	
	public Node getBackground() { return physicalHit.getBackground(); }
	public Node getForeground() { return this.node; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final double originX = origin.getX();
		final double originY = origin.getY();
		final double targetX = target.getX();
		final double targetY = target.getY();
		final double deltaX = targetX - originX;
		final double deltaY = targetY - originY;
		final double deltaDistance = Math.sqrt(
			Math.abs(targetX + targetX) * Math.abs(targetY + targetY)
		);
		final double controlX = originX + deltaX / 2;
		final double controlY = originY + deltaY / 2 - Math.abs(deltaX) * arrowArcMultiplier;
		final Duration duration = Duration.millis(deltaDistance / arrowSpeed);
		
		Shape arrowPath = new QuadCurve(
			originX, originY,
			controlX, controlY,
			targetX, targetY
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
			new ParallelTransition(
				arrowAnimation,
				panAnimation
			),
			setArrowInvisible,
			physicalHit.getAnimation(
				origin,
				target,
				new PauseTransition(Duration.ZERO),
				hpAndShakeAnimation
			)
		);
	}
}
