package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;

/**
 * Probably the simplest demonstration of spell origins and spell targets
 */
public final class Lazor implements SpellAnimationGroup {
	
	private static final Duration duration = Duration.seconds(0.5);
	private static final Duration followerDelay = Duration.seconds(1);
	private static final int crossSectionWidth = 20;
	
	
	private final Path node;
	private final DoubleProperty originX;
	private final DoubleProperty originY;
	private final DoubleProperty targetX;
	private final DoubleProperty targetY;
	
	private final MoveTo followerLeft;
	private final LineTo followerRight;
	private final LineTo leaderLeft;
	private final LineTo leaderRight;
	
	
	public Lazor(Color color) {
		this.originX = new SimpleDoubleProperty(200);
		this.originY = new SimpleDoubleProperty(200);
		this.targetX = new SimpleDoubleProperty(300);
		this.targetY = new SimpleDoubleProperty(300);
		
		this.followerLeft = new MoveTo();
		this.followerRight = new LineTo();
		this.leaderLeft = new LineTo();
		this.leaderRight = new LineTo();
		this.node = new Path(followerLeft, followerRight, leaderRight, leaderLeft);
		this.node.setFill(color);
		this.node.setStroke(Color.TRANSPARENT);
	}
	
	public Node getNode() { return this.node; }
	
	public void setOrigin(double newX, double newY) {
		this.originX.unbind();
		this.originY.unbind();
		this.originX.set(newX);
		this.originY.set(newY);
	}
	
	public void setTarget(double newX, double newY) {
		this.targetX.unbind();
		this.targetY.unbind();
		this.targetX.set(newX);
		this.targetY.set(newY);
	}
	
	public Animation getAnimation(Animation hpAndShakeAnimation) {
		ObjectBinding<Point2D> crossSectionVector = new ObjectBinding<Point2D>() {
			{
				super.bind(originX);
				super.bind(originY);
				super.bind(targetX);
				super.bind(targetY);
			}
			
			@Override
			protected Point2D computeValue() {
				double deltaX = originX.getValue() - targetX.getValue();
				double deltaY = originY.getValue() - targetY.getValue();
				Point2D delta = new Point2D(deltaX, deltaY);
				Point2D deltaDirection = delta.normalize();
				Point2D crossDirection = new Point2D(-deltaDirection.getY(), deltaDirection.getX());
				return crossDirection.multiply(crossSectionWidth / 2);
			}
		};
		
		final Timeline spellAnimation = new Timeline();
		spellAnimation.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(followerLeft.yProperty(), originY.get() + crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(followerLeft.xProperty(), originX.get() + crossSectionVector.get().getX(), Interpolator.LINEAR),
			new KeyValue(followerRight.yProperty(), originY.get() - crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(followerRight.xProperty(), originX.get() - crossSectionVector.get().getX(), Interpolator.LINEAR),
			new KeyValue(leaderLeft.yProperty(), originY.get() + crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(leaderLeft.xProperty(), originX.get() + crossSectionVector.get().getX(), Interpolator.LINEAR),
			new KeyValue(leaderRight.yProperty(), originY.get() - crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(leaderRight.xProperty(), originX.get() - crossSectionVector.get().getX(), Interpolator.LINEAR)
		));
		spellAnimation.getKeyFrames().add(new KeyFrame(followerDelay,
			new KeyValue(followerLeft.yProperty(), originY.get() + crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(followerLeft.xProperty(), originX.get() + crossSectionVector.get().getX(), Interpolator.LINEAR),
			new KeyValue(followerRight.yProperty(), originY.get() - crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(followerRight.xProperty(), originX.get() - crossSectionVector.get().getX(), Interpolator.LINEAR)
		));
		spellAnimation.getKeyFrames().add(new KeyFrame(duration,
			new KeyValue(leaderLeft.yProperty(), targetY.get() + crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(leaderLeft.xProperty(), targetX.get() + crossSectionVector.get().getX(), Interpolator.LINEAR),
			new KeyValue(leaderRight.yProperty(), targetY.get() - crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(leaderRight.xProperty(), targetX.get() - crossSectionVector.get().getX(), Interpolator.LINEAR)
		));
		spellAnimation.getKeyFrames().add(new KeyFrame(duration.add(followerDelay),
			new KeyValue(followerLeft.yProperty(), targetY.get() + crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(followerLeft.xProperty(), targetX.get() + crossSectionVector.get().getX(), Interpolator.LINEAR),
			new KeyValue(followerRight.yProperty(), targetY.get() - crossSectionVector.get().getY(), Interpolator.LINEAR),
			new KeyValue(followerRight.xProperty(), targetX.get() - crossSectionVector.get().getX(), Interpolator.LINEAR)
		));
		
		return new ParallelTransition(
			spellAnimation,
			new SequentialTransition(
				new PauseTransition(duration),
				hpAndShakeAnimation
			)
		);
	}
	
}
