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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.util.Duration;

/**
 * Creates animations that control two properties (nominally x and y
 * coordinates) such that each subsequent animation starts with the
 * values of the previous animation.
 * <p>
 * Particularly useful when the properties are a Translate's x and y properties
 */
public final class BattlePanAnimations {
	private static final double distanceWithNoPan = 40;
	
	private final WritableDoubleValue propertyX;
	private final WritableDoubleValue propertyY;
	private double currentX;
	private double currentY;
	private final double attackerX;
	private final double attackerY;
	private final double defenderX;
	private final double defenderY;
	
	public BattlePanAnimations(
		WritableDoubleValue propertyX,
		WritableDoubleValue propertyY,
		double currentX,
		double currentY
	) {
		this(propertyX, propertyY, currentX, currentY, 0, 0, 0, 0);
	}
	
	private BattlePanAnimations(
		WritableDoubleValue propertyX,
		WritableDoubleValue propertyY,
		double currentX,
		double currentY,
		double attackerX,
		double attackerY,
		double defenderX,
		double defenderY
	) {
		this.propertyX = propertyX;
		this.propertyY = propertyY;
		this.currentX = currentX;
		this.currentY = currentY;
		this.attackerX = attackerX;
		this.attackerY = attackerY;
		this.defenderX = defenderX;
		this.defenderY = defenderY;
	}
	
	private Animation panTo(final double newX, final double newY, /* @Nullable */ final Duration duration) {
		final double deltaX = this.currentX - newX;
		final double deltaY = this.currentY - newY;
		final double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		if (distance >= distanceWithNoPan) {
			final Duration duration2 = (null == duration ? Duration.millis(distance) : duration); 
			
			Animation retval = new Timeline(
				new KeyFrame(Duration.ZERO,
					new KeyValue(propertyX, currentX, Interpolator.LINEAR),
					new KeyValue(propertyY, currentY, Interpolator.LINEAR)
				),
				new KeyFrame(duration2,
					new KeyValue(propertyX, newX, Interpolator.LINEAR),
					new KeyValue(propertyY, newY, Interpolator.LINEAR)
				)
			);
			this.currentX = newX;
			this.currentY = newY;
			
			return retval;
		} else {
			if (null == duration) {
				return Animations.nil();
			} else {
				return new PauseTransition(duration);
			}
		}
	}
	
	public Animation panToAttacker() {return this.panTo(attackerX, attackerY, null);}
	public Animation panToDefender() {return this.panTo(defenderX, defenderY, null);}
	public Animation panToAttacker(Duration d) {return this.panTo(attackerX, attackerY, d);}
	public Animation panToDefender(Duration d) {return this.panTo(defenderX, defenderY, d);}
	public Animation panToAttackerRelative(double dx, double dy, Duration dur) {return this.panTo(attackerX + dx, attackerY + dy, dur);}
	public Animation panToDefenderRelative(double dx, double dy, Duration dur) {return this.panTo(defenderX + dx, defenderY + dy, dur);}
	
	public BattlePanAnimations withNewFocusCoords(
		double attackerX,
		double attackerY,
		double defenderX,
		double defenderY
	) {
		return new BattlePanAnimations(
			this.propertyX,
			this.propertyY,
			this.currentX,
			this.currentY,
			attackerX,
			attackerY,
			defenderX,
			defenderY
		);
	}
	
	public static BattlePanAnimations nil() {
		return new BattlePanAnimations(new SimpleDoubleProperty(), new SimpleDoubleProperty(), 0, 0, 0, 0, 0, 0);
	}
}
