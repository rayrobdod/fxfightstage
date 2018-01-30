package name.rayrobdod.fightStage.spellAnimationGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * First time I've used a ListProperty, if nothing else.
 */
public final class Lightning implements SpellAnimationGroup {
	
	private static final Duration initalDelayDur = Duration.seconds(0.3);
	private static final Duration explodeDur = Duration.seconds(0.2);
	private static final Duration fadeOutDur = Duration.seconds(0.5);
	
	private static final Duration initalDelayStartTime = Duration.ZERO;
	private static final Duration initalDelayEndTime = initalDelayStartTime.add(initalDelayDur);
	private static final Duration explodeStartTime = initalDelayEndTime;
	private static final Duration explodeEndTime = explodeStartTime.add(explodeDur);
	private static final Duration fadeOutStartTime = explodeEndTime;
	private static final Duration fadeOutEndTime = fadeOutStartTime.add(fadeOutDur);
	
	private static final double belowSpellTarget = 50;
	private static final double aboveSpellTarget = -500;
	private static final double segmentMaxXDelta = 20;
	private static final double segmentMinXDelta = -20;
	private static final double segmentMaxYDelta = 30;
	private static final double segmentMinYDelta = 5;
	
	private static final Duration totalDuration = fadeOutEndTime;
	
	private final Node background;
	private final Node foreground;
	private final Polyline line1;
	private final Polyline line2;
	private final ListProperty<Double> linePoints;
	
	public Lightning() {
		this.line1 = new Polyline();
		this.line2 = new Polyline();
		
		this.line1.setOpacity(0);
		this.line1.setStroke(Color.hsb(240, 0.2, 0.95, 0.9));
		this.line1.setStrokeWidth(3);
		this.line1.setStrokeLineCap(StrokeLineCap.ROUND);
		this.line1.setEffect(new GaussianBlur(1));
		this.line2.setOpacity(0);
		this.line2.setStroke(Color.hsb(250, 0.1, 0.85, 0.7));
		this.line2.setStrokeWidth(12);
		this.line2.setStrokeLineCap(StrokeLineCap.ROUND);
		this.line2.setEffect(new GaussianBlur(6));
		
		// I don't know what `SimpleListProperty<>(line1)` does, but `line1` is not updated with the property in that case.
		this.linePoints = new SimpleListProperty<>();
		this.linePoints.addListener(reverseBind(line1.getPoints()));
		this.linePoints.addListener(reverseBind(line2.getPoints()));
		
		this.background = new Group();
		this.foreground = new Group(
			this.line1,
			this.line2
		);
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final Random rng = new Random();
		final ObservableList<Double> points = javafx.collections.FXCollections
				.observableList(generateLightningPoints(target));
		
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(linePoints, points, Interpolator.DISCRETE),
			new KeyValue(line1.opacityProperty(), 0.0, Interpolator.DISCRETE),
			new KeyValue(line2.opacityProperty(), 0.0, Interpolator.DISCRETE)
		));
		// Timeline apparently will not touch something without it being mentioned at least twice
		timeline.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(linePoints, points, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(explodeStartTime,
			new KeyValue(line1.opacityProperty(), 1.0, Interpolator.DISCRETE),
			new KeyValue(line2.opacityProperty(), 1.0, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeOutStartTime,
			new KeyValue(line1.opacityProperty(), 1.0, Interpolator.LINEAR),
			new KeyValue(line2.opacityProperty(), 1.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeOutEndTime,
			new KeyValue(line1.opacityProperty(), 0.0, Interpolator.LINEAR),
			new KeyValue(line2.opacityProperty(), 0.0, Interpolator.LINEAR)
		));
		
		return new ParallelTransition(
			panAnimation,
			timeline,
			new SequentialTransition(
				new PauseTransition(explodeStartTime),
				hpAndShakeAnimation
			)
		);
	}
	
	/** Creates a vertical jagged line */
	private static List<Double> generateLightningPoints(final Point2D target) {
		final Random rng = new Random();
		final List<Double> retval = new ArrayList<>();
		
		final double maxY = target.getY() + aboveSpellTarget;
		double currentX = target.getX();
		double currentY = target.getY() + belowSpellTarget;
		do {
			retval.add(currentX);
			retval.add(currentY);
			
			final double dx = (currentY >= target.getY() ? 0.25 : 1) * (segmentMinXDelta + (segmentMaxXDelta - segmentMinXDelta) * rng.nextDouble());
			final double dy = segmentMinYDelta + (segmentMaxYDelta - segmentMinYDelta) * rng.nextDouble();
			
			currentX -= dx;
			currentY -= dy;
		} while (currentY >= maxY);
		
		return retval;
	}
	
	private static ChangeListener<ObservableList<Double>> reverseBind(final ObservableList<Double> list) {
		return (observable, oldValue, newValue) -> list.setAll(newValue);
	}
}
