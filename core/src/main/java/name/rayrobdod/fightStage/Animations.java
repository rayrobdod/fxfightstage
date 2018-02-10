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

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.util.Duration;

/**
 * A collection of static functions that create simple animation
 */
public final class Animations {
	private Animations() {}
	
	/**
	 * Returns an animation that has no effects and which takes zero time
	 */
	public static Animation nil() {
		// can't return a static constant due to ParallelTransition's
		// `java.lang.IllegalArgumentException: Attempting to add a duplicate to the list of children`
		// which is stupid, but I can't change ParallelTransition.
		return new PauseTransition(Duration.ZERO);
	}
	
	/**
	 * A transition that changes the value of the specified property to the
	 * specified value in the shortest amount of time.
	 */
	public static Animation booleanSetAnimation(BooleanProperty property, boolean to) {
		final Timeline retval = new Timeline();
		retval.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(property, !to, Interpolator.DISCRETE)
		));
		retval.getKeyFrames().add(new KeyFrame(Duration.ONE,
			new KeyValue(property, to, Interpolator.DISCRETE)
		));
		return retval;
	}
	
	/**
	 * An animation that changes the value of the specified property between the specified values.
	 */
	public static Animation doubleSimpleAnimation(
		Duration duration,
		DoubleProperty property,
		double fromValue,
		double toValue
	) {
		if (fromValue != toValue) {
			final Timeline retval = new Timeline();
			retval.getKeyFrames().add(new KeyFrame(Duration.ZERO,
				new KeyValue(property, fromValue, Interpolator.DISCRETE)
			));
			retval.getKeyFrames().add(new KeyFrame(duration,
				new KeyValue(property, toValue, Interpolator.LINEAR)
			));
			return retval;
		} else {
			// Timeline does not like when a value doesn't change over the Timeline's duration
			// Using a timeline in that case will break things; this isn't due to an optimization attempt
			return new PauseTransition(duration);
		}
	}
	
	/**
	 * An animation that changes the value of the specified property between the specified values.
	 */
	public static Animation integerSimpleAnimation(
		Duration duration,
		IntegerProperty property,
		int fromValue,
		int toValue
	) {
		if (fromValue != toValue) {
			final Timeline retval = new Timeline();
			retval.getKeyFrames().add(new KeyFrame(Duration.ZERO,
				new KeyValue(property, fromValue, Interpolator.DISCRETE)
			));
			retval.getKeyFrames().add(new KeyFrame(duration,
				new KeyValue(property, toValue, Interpolator.LINEAR)
			));
			return retval;
		} else {
			// Timeline does not like when a value doesn't change over the Timeline's duration
			// Using a timeline in that case will break things; this isn't due to an optimization attempt
			return new PauseTransition(duration);
		}
	}
}
