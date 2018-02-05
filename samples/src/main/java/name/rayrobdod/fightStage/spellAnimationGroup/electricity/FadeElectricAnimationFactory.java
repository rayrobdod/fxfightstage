/*
 * Copyright 2018 Raymond Dodge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.rayrobdod.fightStage.spellAnimationGroup.electricty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * An animation which consists of the provided line appearing suddenly,
 * then fading to transparent gradually
 */
public final class FadeElectricAnimationFactory implements ElectricAnimationFactory {
	private static final Duration initalDelayDur = Duration.seconds(0.3);
	private static final Duration explodeDur = Duration.seconds(0.2);
	private static final Duration fadeOutDur = Duration.seconds(0.5);
	
	private static final Duration initalDelayStartTime = Duration.ZERO;
	private static final Duration initalDelayEndTime = initalDelayStartTime.add(initalDelayDur);
	private static final Duration explodeStartTime = initalDelayEndTime;
	private static final Duration explodeEndTime = explodeStartTime.add(explodeDur);
	private static final Duration fadeOutStartTime = explodeEndTime;
	private static final Duration fadeOutEndTime = fadeOutStartTime.add(fadeOutDur);
	
	private final Polyline sharpLine;
	private final Polyline blurredLine;
	private final WritableValue<List<Double>> sharpLinePoints;
	private final WritableValue<List<Double>> blurredLinePoints;
	private final JaggedLineFactory lineGenerator;
	
	public FadeElectricAnimationFactory(
		  final JaggedLineFactory lineGenerator
		, final Group foreground
		, final Group background
	) {
		this.lineGenerator = lineGenerator;
		
		this.sharpLine = new Polyline();
		this.sharpLine.setOpacity(0);
		this.sharpLine.setStroke(Color.hsb(240, 0.2, 0.95, 0.9));
		this.sharpLine.setStrokeWidth(3);
		this.sharpLine.setStrokeLineCap(StrokeLineCap.ROUND);
		this.sharpLine.setEffect(new GaussianBlur(1));
		
		this.blurredLine = new Polyline();
		this.blurredLine.setOpacity(0);
		this.blurredLine.setStroke(Color.hsb(250, 0.1, 0.85, 0.7));
		this.blurredLine.setStrokeWidth(12);
		this.blurredLine.setStrokeLineCap(StrokeLineCap.ROUND);
		this.blurredLine.setEffect(new GaussianBlur(6));
		this.blurredLine.setBlendMode(BlendMode.SCREEN);
		
		this.sharpLinePoints = new WritableObservableListWrapper<>(sharpLine.getPoints());
		this.blurredLinePoints = new WritableObservableListWrapper<>(blurredLine.getPoints());
		
		foreground.getChildren().add(sharpLine);
		foreground.getChildren().add(blurredLine);
	}
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		final List<Double> coords = points2coordinates(lineGenerator.build(origin, target));
		
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(sharpLinePoints, coords, Interpolator.DISCRETE),
			new KeyValue(blurredLinePoints, coords, Interpolator.DISCRETE),
			new KeyValue(sharpLine.opacityProperty(), 0.0, Interpolator.DISCRETE),
			new KeyValue(blurredLine.opacityProperty(), 0.0, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(explodeStartTime,
			new KeyValue(sharpLine.opacityProperty(), 1.0, Interpolator.DISCRETE),
			new KeyValue(blurredLine.opacityProperty(), 1.0, Interpolator.DISCRETE)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeOutStartTime,
			new KeyValue(sharpLine.opacityProperty(), 1.0, Interpolator.LINEAR),
			new KeyValue(blurredLine.opacityProperty(), 1.0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(fadeOutEndTime,
			new KeyValue(sharpLinePoints, coords, Interpolator.DISCRETE),
			new KeyValue(blurredLinePoints, coords, Interpolator.DISCRETE),
			new KeyValue(sharpLine.opacityProperty(), 0.0, Interpolator.LINEAR),
			new KeyValue(blurredLine.opacityProperty(), 0.0, Interpolator.LINEAR)
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
	
	/**
	 * Converts a list of points into the format {@link javafx.scene.shape.Polyline} likes
	 */
	private static List<Double> points2coordinates(List<Point2D> ps) {
		return ps.stream().flatMap(p -> Stream.of(p.getX(), p.getY())).collect(Collectors.toList());
	}
}
