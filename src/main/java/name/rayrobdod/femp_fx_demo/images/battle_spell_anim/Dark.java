package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;

public final class Dark implements SpellAnimationGroup {
	
	private static final Duration fadeInTime = Duration.seconds(0.3);
	private static final Duration fadeOutTime = Duration.seconds(0.2);
	private static final Duration stayTime = Duration.seconds(0.5);
	private static final Duration endDelayTime = Duration.seconds(0.1);
	private static final Color mainColor = Color.rgb(224, 196, 255);
	private static final int mainRadius = 80;
	private static final double defaultCenterX = 200;
	private static final double defaultCenterY = 200;
	
	
	private final Circle node;
	
	public Dark() {
		this.node = new Circle();
		this.node.setFill(Color.BLACK);
		this.node.setBlendMode(javafx.scene.effect.BlendMode.EXCLUSION);
	}
	
	public Node getNode() { return this.node; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(this.node.centerXProperty(), target.getX(), Interpolator.DISCRETE),
			new KeyValue(this.node.centerYProperty(), target.getY(), Interpolator.DISCRETE),
			new KeyValue(this.node.fillProperty(), Color.BLACK, Interpolator.DISCRETE),
			new KeyValue(this.node.radiusProperty(), mainRadius, Interpolator.DISCRETE)
		));
		// Timeline apparently will not touch something without it being mentioned at least twice
		timeline.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(this.node.centerXProperty(), target.getX(), Interpolator.DISCRETE),
			new KeyValue(this.node.centerYProperty(), target.getY(), Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeInTime,
			new KeyValue(this.node.fillProperty(), mainColor, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeInTime.add(stayTime),
			new KeyValue(this.node.radiusProperty(), mainRadius, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeInTime.add(stayTime).add(fadeOutTime),
			new KeyValue(this.node.radiusProperty(), 0, Interpolator.LINEAR)
		));
		
		return new SequentialTransition(
			panAnimation,
			timeline,
			new ParallelTransition(
				hpAndShakeAnimation,
				new PauseTransition(endDelayTime)
			)
		);
	}
	
}
