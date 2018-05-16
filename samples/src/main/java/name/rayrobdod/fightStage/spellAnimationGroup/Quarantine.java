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

import java.util.List;
import java.util.function.ToDoubleFunction;
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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.WritableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 * I guess this one uses more higher-level primitives than the other
 * animations. Just another animation.
 */
public final class Quarantine implements SpellAnimationGroup {
	
	private static final double radius = 80;
	private static final double overshoot = 20;
	private static final int pointCount = 3;
	private static final double rotationOffset = 0;
	
	private static final Color cutStroke = Color.rgb(220, 180, 40);
	private static final double cutWidth = 3;
	private static final Color drillFill = Color.rgb(240, 220, 80);
	private static final double drillRadius = 6;
	private static final Color fillFill = Color.rgb(255, 240, 200, 0.8);
	
	private static final Duration drillFadeInDur = Duration.seconds(0.2);
	private static final Duration fillFadeInDur = Duration.seconds(0.2);
	private static final Duration cutDur = Duration.seconds(0.8);
	private static final Duration drillFadeOutDur = Duration.seconds(0.2);
	private static final Duration fillFadeOutDur = Duration.seconds(0.2);
	
	private final Node background;
	private final Group foreground;
	private final ObjectProperty<Point2D> target;
	private final List<DoubleProperty> drillOffsetXs;
	private final List<DoubleProperty> drillOffsetYs;
	private final DoubleProperty drillOpacity;
	private final DoubleProperty cutOpacity;
	private final DoubleProperty fillOpacity;
	private final WritableObservableListWrapper<Double> fillPoints;
	
	public Quarantine() {
		this.background = new Group();
		this.foreground = new Group();
		this.drillOffsetXs = Stream.generate(SimpleDoubleProperty::new).limit(pointCount).collect(Collectors.toList());
		this.drillOffsetYs = Stream.generate(SimpleDoubleProperty::new).limit(pointCount).collect(Collectors.toList());
		this.drillOpacity = new SimpleDoubleProperty(0.0);
		this.cutOpacity = new SimpleDoubleProperty(0.0);
		this.fillOpacity = new SimpleDoubleProperty(0.0);
		this.target = new SimpleObjectProperty<>(new Point2D(0,0));
		
		final ObjectBinding<Color> drillFillProp = new ColorAlphaBinding(drillFill, drillOpacity);
		final ObjectBinding<Color> cutStrokeProp = new ColorAlphaBinding(cutStroke, cutOpacity);
		final ObjectBinding<Color> fillFillProp = new ColorAlphaBinding(fillFill, fillOpacity);
		
		{
			final Polygon fill = new Polygon();
			fill.setStroke(Color.TRANSPARENT);
			fill.fillProperty().bind(fillFillProp);
			
			this.fillPoints = new WritableObservableListWrapper<>(fill.getPoints());
			this.foreground.getChildren().add(fill);
		}
		
		for (int i = 0; i < pointCount; i++) {
			final Point2D vertex1 = vertexOffset(i);
			final Point2D vertex2 = vertexOffset(i + 1);
			final DoubleBinding targetX = new MapToDoubleBinding<>(target, p -> p.getX());
			final DoubleBinding targetY = new MapToDoubleBinding<>(target, p -> p.getY());
			final DoubleProperty curX = this.drillOffsetXs.get(i);
			final DoubleProperty curY = this.drillOffsetYs.get(i);
			
			final Line cut = new Line();
			cut.setFill(Color.TRANSPARENT);
			cut.strokeProperty().bind(cutStrokeProp);
			cut.setStrokeWidth(cutWidth);
			
			cut.startXProperty().bind(targetX.add(new StartCutBinding(curX, vertex1.getX(), vertex2.getX())));
			cut.startYProperty().bind(targetY.add(new StartCutBinding(curY, vertex1.getY(), vertex2.getY())));
			cut.endXProperty().bind(targetX.add(new EndCutBinding(curX, vertex1.getX(), vertex2.getX())));
			cut.endYProperty().bind(targetY.add(new EndCutBinding(curY, vertex1.getY(), vertex2.getY())));
			
			this.foreground.getChildren().add(cut);
		}
		
		for (int i = 0; i < pointCount; i++) {
			final DoubleBinding targetX = new MapToDoubleBinding<>(target, p -> p.getX());
			final DoubleBinding targetY = new MapToDoubleBinding<>(target, p -> p.getY());
			
			final Circle drill = new Circle();
			drill.setRadius(drillRadius);
			drill.setStroke(Color.TRANSPARENT);
			drill.fillProperty().bind(drillFillProp);
			drill.centerXProperty().bind(targetX.add(drillOffsetXs.get(i)));
			drill.centerYProperty().bind(targetY.add(drillOffsetYs.get(i)));
			
			this.foreground.getChildren().add(drill);
		}
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final Animation setTargetAnim = setAnimation(this.target, target);
		final Animation setPolygonAnim = setAnimation(this.fillPoints, polygonPoints(target));
		final Animation drillFadeInAnim = Animations.simpleAnimation(this.drillFadeInDur, drillOpacity, 0.0, 1.0);
		final Animation cutFadeInAnim = setDoubleAnimation(this.cutOpacity, 1.0);
		final Animation fillFadeInAnim = Animations.simpleAnimation(this.fillFadeInDur, fillOpacity, 0.0, 1.0);
		final Animation drillFadeOutAnim = Animations.simpleAnimation(this.drillFadeOutDur, drillOpacity, 1.0, 0.0);
		final Animation cutFadeOutAnim = Animations.simpleAnimation(this.fillFadeOutDur, cutOpacity, 1.0, 0.0);
		final Animation fillFadeOutAnim = Animations.simpleAnimation(this.fillFadeOutDur, fillOpacity, 1.0, 0.0);
		
		final Timeline cutAnim = new Timeline();
		for (int i = 0; i < pointCount; i++) {
			final Point2D vertex1 = vertexOffset(i);
			final Point2D vertex2 = vertexOffset(i + 1);
			
			final Point2D startPoint = vertex1.add(vertex1.subtract(vertex2).multiply(overshoot / radius));
			final Point2D endPoint = vertex2.add(vertex2.subtract(vertex1).multiply(overshoot / radius));
			
			cutAnim.getKeyFrames().add(new KeyFrame(Duration.ZERO,
				new KeyValue(drillOffsetXs.get(i), startPoint.getX(), Interpolator.LINEAR),
				new KeyValue(drillOffsetYs.get(i), startPoint.getY(), Interpolator.LINEAR)
			));
			cutAnim.getKeyFrames().add(new KeyFrame(cutDur,
				new KeyValue(drillOffsetXs.get(i), endPoint.getX(), Interpolator.LINEAR),
				new KeyValue(drillOffsetYs.get(i), endPoint.getY(), Interpolator.LINEAR)
			));
		}
		
		return new SequentialTransition(
			panAnimation.panToDefender(),
			setTargetAnim,
			new ParallelTransition(
				new SequentialTransition(
					drillFadeInAnim,
					cutFadeInAnim
				),
				cutAnim,
				new SequentialTransition(
					new PauseTransition(cutDur.subtract(drillFadeOutDur)),
					drillFadeOutAnim
				)
			),
			new ParallelTransition(
				setPolygonAnim,
				new SequentialTransition(
					fillFadeInAnim,
					fillFadeOutAnim
				),
				shakeAnimation.apply(4, fillFadeInAnim.getTotalDuration().add(fillFadeOutAnim.getTotalDuration())),
				hitAnimation
			),
			cutFadeOutAnim
		);
	}
	
	private static Point2D vertexOffset(int index) {
		final double angle = Math.PI * 2 * index / pointCount;
		return new Point2D(
			radius * Math.sin(angle),
			radius * Math.cos(angle)
		);
	}
	
	private static List<Double> polygonPoints(Point2D target) {
		final List<Double> retval = new java.util.ArrayList<>();
		for (int i = 0; i < pointCount; i++) {
			final Point2D p = vertexOffset(i);
			retval.add(target.getX() + p.getX());
			retval.add(target.getY() + p.getY());
		}
		return retval;
	}
	
	private final static class ColorAlphaBinding extends ObjectBinding<Color> {
		private final Color base;
		private final ObservableDoubleValue alpha;
		
		public ColorAlphaBinding(Color base, ObservableDoubleValue alpha) {
			this.base = base;
			this.alpha = alpha;
			super.bind(alpha);
		}
		
		@Override protected Color computeValue() {
			return this.base.deriveColor(0.0, 1.0, 1.0, this.alpha.get());
		}
	}
	
	private final static class MapToDoubleBinding<A> extends DoubleBinding {
		private final ObservableObjectValue<A> value;
		private final ToDoubleFunction<A> mapping;
		
		public MapToDoubleBinding(ObservableObjectValue<A> value, ToDoubleFunction<A> mapping) {
			this.mapping = mapping;
			this.value = value;
			super.bind(value);
		}
		
		@Override protected double computeValue() {
			return this.mapping.applyAsDouble(this.value.get());
		}
	}
	
	private final static class ClipDoubleBinding extends DoubleBinding {
		private final ObservableDoubleValue value;
		private final ObservableDoubleValue clip;
		private final ObservableDoubleValue side;
		
		public ClipDoubleBinding(ObservableDoubleValue value, ObservableDoubleValue clip, ObservableDoubleValue side) {
			this.value = value;
			this.clip = clip;
			this.side = side;
			super.bind(value);
			super.bind(clip);
			super.bind(side);
		}
		
		@Override protected double computeValue() {
			final double current = this.value.get();
			final double clip = this.clip.get();
			final double side = this.side.get();
			return ( (current > clip) == (side > 0.0) ? current : clip );
		}
	}
	
	private final static class StartCutBinding extends DoubleBinding {
		private final ObservableDoubleValue drill;
		private final double start;
		private final double end;
		
		public StartCutBinding(ObservableDoubleValue drill, double start, double end) {
			this.drill = drill;
			this.start = start;
			this.end = end;
			super.bind(drill);
		}
		
		@Override protected double computeValue() {
			final double drill = this.drill.get();
			return ( (end > start) == (drill > start) ? start : drill );
		}
	}
	
	private final static class EndCutBinding extends DoubleBinding {
		private final ObservableDoubleValue drill;
		private final double start;
		private final double end;
		
		public EndCutBinding(ObservableDoubleValue drill, double start, double end) {
			this.drill = drill;
			this.start = start;
			this.end = end;
			super.bind(drill);
		}
		
		@Override protected double computeValue() {
			final double drill = this.drill.get();
			return ( (start > end) == (drill > end) ? drill : end );
		}
	}
	
	public static <A> Animation setAnimation(WritableValue<A> property, A to) {
		final Timeline retval = new Timeline();
		retval.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(property, to, Interpolator.DISCRETE)
		));
		retval.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(property, to, Interpolator.DISCRETE)
		));
		return retval;
	}
	
	public static Animation setDoubleAnimation(DoubleProperty property, double to) {
		final Timeline retval = new Timeline();
		retval.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(property, to, Interpolator.DISCRETE)
		));
		retval.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(property, to, Interpolator.DISCRETE)
		));
		return retval;
	}
}
