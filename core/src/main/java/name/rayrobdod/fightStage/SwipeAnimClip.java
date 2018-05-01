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
package name.rayrobdod.fightStage;

import static name.rayrobdod.fightStage.PathElements.newBoundHLineTo;
import static name.rayrobdod.fightStage.PathElements.newBoundMoveTo;
import static name.rayrobdod.fightStage.PathElements.newBoundVLineTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.util.Duration;

/**
 * Used as another Node's clip
 */
final class SwipeAnimClip {
	
	private final Path node;
	/** Property has max 1.0 and min 0.0 */
	private final List<DoubleProperty> leftXs;
	/** Property has max 1.0 and min 0.0 */
	private final List<DoubleProperty> rightXs;
	
	
	private static final Duration variability = Duration.millis(500);
	private static final Duration duration = Duration.millis(750);
	private static final int segments = 24;
	
	/**
	 */
	public SwipeAnimClip(
		  DoubleExpression width
		, DoubleExpression height
	) {
		this.leftXs = Stream.generate(() -> new SimpleDoubleProperty(0))
				.limit(segments)
				.collect(Collectors.toList());
		this.rightXs = Stream.generate(() -> new SimpleDoubleProperty(0))
				.limit(segments)
				.collect(Collectors.toList());
		
		final List<PathElement> path = new ArrayList<>();
		
		path.add(newBoundMoveTo(
			  this.rightXs.get(0).multiply(width)
			, ZERO
		));
		
		for (int i = 0; i < this.leftXs.size(); i++) {
			path.add(newBoundHLineTo(this.leftXs.get(i).multiply(width)));
			path.add(newBoundVLineTo(height.multiply(i + 1).divide(this.leftXs.size())));
		}
		for (int i = this.rightXs.size() - 1; i >= 0; i--) {
			path.add(newBoundHLineTo(this.rightXs.get(i).multiply(width)));
			path.add(newBoundVLineTo(height.multiply(i).divide(this.rightXs.size())));
		}
		
		this.node = new Path(path);
		this.node.setFill(Color.BLACK);
		this.node.setStroke(Color.TRANSPARENT);
	}
	
	/**
	 * Returns the node associated with this component.
	 * The object returned has the same identity each time.
	 */
	public Node getNode() { return this.node; }
	
	public Animation swipeInAnimation() {
		return anim(rightXs, 1.0, leftXs, 1.0, 0.0);
	}
	public Animation swipeOutAnimation() {
		return anim(leftXs, 0.0, rightXs, 1.0, 0.0);
	}
	
	private static Animation anim(
		  List<DoubleProperty> staticProps
		, double staticValue
		, List<DoubleProperty> dynamicProps
		, double dynamicFrom
		, double dynamicTo
	) {
		final Random rng = new Random();
		final List<KeyFrame> timeline = new ArrayList<>();
		
		timeline.add(new KeyFrame(Duration.ZERO,
			staticProps.stream().map(x -> new KeyValue(x, staticValue, Interpolator.LINEAR)).toArray(KeyValue[]::new)
		));
		timeline.add(new KeyFrame(Duration.ZERO,
			dynamicProps.stream().map(x -> new KeyValue(x, dynamicFrom, Interpolator.LINEAR)).toArray(KeyValue[]::new)
		));
		timeline.add(new KeyFrame(duration.add(variability),
			staticProps.stream().map(x -> new KeyValue(x, staticValue, Interpolator.LINEAR)).toArray(KeyValue[]::new)
		));
		
		dynamicProps.forEach(x -> {
			final double rand1 = rng.nextDouble();
			final double rand2 = rng.nextDouble();
			timeline.add(new KeyFrame(variability.multiply(rand1), new KeyValue(x, dynamicFrom, Interpolator.LINEAR)));
			timeline.add(new KeyFrame(duration.add(variability.multiply(rand2)), new KeyValue(x, dynamicTo, Interpolator.LINEAR)));
		});
		
		return new Timeline(timeline.stream().toArray(KeyFrame[]::new));
	}
	
	/**
	 * An immutable ObservableDoubleValue that has a value of {@code 0.0}
	 */
	private final static ObservableDoubleValue ZERO = new ObservableDoubleValue() {
		public double get() {return 0;}
		public Double getValue() {return 0.0;}
		public double doubleValue() {return 0;}
		public float floatValue() {return 0;}
		public int intValue() {return 0;}
		public long longValue() {return 0;}
		
		public void addListener(javafx.beans.InvalidationListener listener) {}
		public void removeListener(javafx.beans.InvalidationListener listener) {}
		public void addListener(javafx.beans.value.ChangeListener<? super Number> listener) {}
		public void removeListener(javafx.beans.value.ChangeListener<? super Number> listener) {}
	};
	
}
