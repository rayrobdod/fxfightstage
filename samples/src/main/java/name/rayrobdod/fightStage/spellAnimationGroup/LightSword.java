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
package name.rayrobdod.fightStage.spellAnimationGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * A spell animation that causes an ethereal sword drop on the opponent.
 * <p>
 * Doesn't show off anything special.
 */
public final class LightSword implements SpellAnimationGroup {
	
	private static final Duration inDur = Duration.millis(400);
	private static final Duration holdDur = Duration.millis(200);
	private static final Duration outDur = Duration.millis(150);
	
	private static final Duration inStartTime = Duration.ZERO;
	private static final Duration holdStartTime = inStartTime.add(inDur);
	private static final Duration outStartTime = holdStartTime.add(holdDur);
	private static final Duration endTime = outStartTime.add(outDur);
	
	/*
	 * I need a static list of 'raw' path points to do calculations on.
	 * I don't like separating the X coordinate from  the y coordinate from
	 * the type of path element, but doing otherwise feels like too much effort.
	 */
	private static final double[] PATH_X = {
			0,
			15, 15, 15,
			15, 12.5,
			25,
			25,
			5,
			5,
			
			-5,
			-5,
			-25,
			-25,
			-12.5,
			-15, -15,
			-15, -15, 0
	};
	private static final double[] PATH_Y = {
			0,
			30, 45, 55,
			65, 80,
			80,
			90,
			90,
			100,
			
			100,
			90,
			90,
			80,
			80,
			65, 55,
			45, 30, 0
	};
	
	private static final double initOffsetY = 75;
	private static final double overtimeFramerate = 15;
	
	private final Node background;
	private final Node backLayer;
	private final Path frontLayer;
	/** The x-control-point properties found in the frontLayer, in order */
	private final List<DoubleProperty> xs;
	/** The y-control-point properties found in the frontLayer, in order */
	private final List<DoubleProperty> ys;
	
	
	public LightSword() {
		final List<PathElement> pathParts = java.util.Arrays.asList(
			new MoveTo(),
			new CubicCurveTo(),
			new QuadCurveTo(),
			new LineTo(),
			new LineTo(),
			new LineTo(),
			new LineTo(),
			
			new LineTo(),
			new LineTo(),
			new LineTo(),
			new LineTo(),
			new LineTo(),
			new QuadCurveTo(),
			new CubicCurveTo()
		);
		
		PairListDoubleProperty pointProps = pathProps(pathParts);
		this.xs = pointProps.xs;
		this.ys = pointProps.ys;
		
		this.backLayer = new Rectangle();
		this.background = new Rectangle();
		
		this.frontLayer = new Path(pathParts);
		this.frontLayer.setFill(Color.CORNSILK);
		this.frontLayer.setStroke(Color.TRANSPARENT);
		this.frontLayer.setOpacity(0.9);
		this.frontLayer.setEffect(new GaussianBlur());
	}
	
	public Node objectBehindLayer() { return this.backLayer; }
	public Node objectFrontLayer() { return this.frontLayer; }
	public Node backgroundLayer() { return this.background; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(swordKeyFrame(inStartTime, target, 0, initOffsetY, 0));
		timeline.getKeyFrames().add(swordKeyFrame(holdStartTime, target, 1, initOffsetY, 0));
		timeline.getKeyFrames().add(swordKeyFrame(outStartTime, target, 1, initOffsetY, 0));
		timeline.getKeyFrames().add(swordKeyFrame(endTime, target, 1, 0, 0));
		
		final Timeline overtimeline = new Timeline();
		{
			final double ySpeedPerFrame = initOffsetY / (outDur.toSeconds() * overtimeFramerate);
			final int frames = (int) (100 / ySpeedPerFrame) + 1;
			final Duration overtimeFramerate2 = Duration.seconds(1.0 / overtimeFramerate);
			for (int i = 0; i < frames; i++) {
				overtimeline.getKeyFrames().add(swordKeyFrame(
					overtimeFramerate2.multiply(i),
					target,
					1,
					-ySpeedPerFrame * i,
					0
				));
			}
		}
		
		return new SequentialTransition(
			panAnimation.panToDefender(),
			timeline,
			new ParallelTransition(
				overtimeline,
				shakeAnimation.apply(),
				hitAnimation
			)
		);
	}
	
	/**
	 * Extracts, from the path, a sequence of control point properties in the order that they are used by the path
	 * @return said list
	 * @note does not support all subclasses of PathElement.
	 */
	private static PairListDoubleProperty pathProps(List<PathElement> parts) {
		List<DoubleProperty> xProps = new ArrayList<>();
		List<DoubleProperty> yProps = new ArrayList<>();
		
		for (PathElement e : parts) {
			if (e instanceof MoveTo) {
				MoveTo e2 = (MoveTo) e;
				xProps.add(e2.xProperty());
				yProps.add(e2.yProperty());
			} else if (e instanceof LineTo) {
				LineTo e2 = (LineTo) e;
				xProps.add(e2.xProperty());
				yProps.add(e2.yProperty());
			} else if (e instanceof QuadCurveTo) {
				QuadCurveTo e2 = (QuadCurveTo) e;
				xProps.add(e2.controlXProperty());
				yProps.add(e2.controlYProperty());
				xProps.add(e2.xProperty());
				yProps.add(e2.yProperty());
			} else if (e instanceof CubicCurveTo) {
				CubicCurveTo e2 = (CubicCurveTo) e;
				xProps.add(e2.controlX1Property());
				yProps.add(e2.controlY1Property());
				xProps.add(e2.controlX2Property());
				yProps.add(e2.controlY2Property());
				xProps.add(e2.xProperty());
				yProps.add(e2.yProperty());
			} else {
				throw new IllegalArgumentException(e.getClass().getName());
			}
		}
		assert (xProps.size() == PATH_X.length);
		assert (yProps.size() == PATH_Y.length);
		
		PairListDoubleProperty retval = new PairListDoubleProperty();
		retval.xs = Collections.unmodifiableList(xProps);
		retval.ys = Collections.unmodifiableList(yProps);
		return retval;
	}
	
	private static final class PairListDoubleProperty {
		public List<DoubleProperty> xs;
		public List<DoubleProperty> ys;
	}
	
	/**
	 * @param time the time value of the new KeyFrame
	 * @param target the location of the spell target
	 * @param xMultiplier the scaling of the x-values assigned in this frame
	 * @param yOffset the offset of the y-values assigned in this frame
	 * @param yMin the minimum y-value assigned in this frame
	 */
	private KeyFrame swordKeyFrame(
		final Duration time,
		final Point2D target,
		final double xMultiplier,
		final double yOffset,
		final double yMin
	) {
		final List<KeyValue> kvs = new ArrayList<>();
		
		for (int i = 0; i < xs.size(); i++) {
			final DoubleProperty prop = xs.get(i);
			final double raw = PATH_X[i % PATH_X.length];
			final double value = target.getX() + raw * xMultiplier;
			kvs.add(new KeyValue(prop, value, Interpolator.LINEAR));
		}
		for (int i = 0; i < ys.size(); i++) {
			final DoubleProperty prop = ys.get(i);
			final double raw = PATH_Y[i % PATH_Y.length];
			final double value = target.getY() - Math.max(yMin, raw + yOffset);
			kvs.add(new KeyValue(prop, value, Interpolator.LINEAR));
		}
		
		final KeyValue[] kvs2 = kvs.toArray(new KeyValue[0]);
		return new KeyFrame(time, kvs2);
	}
}
