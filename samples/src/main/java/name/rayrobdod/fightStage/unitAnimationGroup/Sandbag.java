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
package name.rayrobdod.fightStage.unitAnimationGroup;

import static name.rayrobdod.fightStage.PathElements.newBoundCubicCurveTo;
import static name.rayrobdod.fightStage.PathElements.newBoundLineTo;
import static name.rayrobdod.fightStage.PathElements.newBoundMoveTo;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.UnitAnimationGroup;

public final class Sandbag implements UnitAnimationGroup {
	
	private final Group node;
	private final DoubleProperty scaleXProp;
	private final DoubleProperty moveXProp;
	private final DoubleProperty moveYProp;
	private final DoubleProperty indentProp;
	private final int width;
	private final int height;
	
	public Sandbag(final int width, final int height) {
		this.width = width;
		this.height = height;
		
		this.scaleXProp = new SimpleDoubleProperty(1.0);
		this.moveXProp = new SimpleDoubleProperty(0.0);
		this.moveYProp = new SimpleDoubleProperty(0.0);
		this.indentProp = new SimpleDoubleProperty(0.0);
		
		final double yControlDelta = width * 0.3;
		final DoubleBinding frontX = moveXProp.add(scaleXProp.multiply(-width / 2));
		final DoubleBinding midX = moveXProp.add(0);
		final DoubleBinding backX = moveXProp.add(scaleXProp.multiply(width / 2));
		final DoubleBinding backControlX = backX.add(scaleXProp.multiply(indentProp));
		final DoubleBinding frontControlX = frontX.add(scaleXProp.multiply(indentProp));
		final DoubleBinding foldX = frontControlX.add(scaleXProp.multiply(indentProp.divide(2)));
		
		final DoubleBinding topY = moveYProp.add(-height);
		final DoubleBinding midY = moveYProp.add(-height / 2);
		final DoubleBinding bottomY = moveYProp.add(0);
		final DoubleBinding bottomControlY = bottomY.add(yControlDelta);
		final DoubleBinding topControl1Y = topY.add(yControlDelta);
		final DoubleBinding topControl2Y = topY.add(-yControlDelta);
		final DoubleBinding fold1Y = midY.add(height / 12);
		final DoubleBinding fold2Y = midY.add(-height / 36);
		
		final DoubleBinding topFrontY = topY.add(indentProp.divide(3));
		
		final Path fill = new Path(
			newBoundMoveTo(frontX, bottomY),
			newBoundCubicCurveTo(frontX, bottomControlY, backX, bottomControlY, backX, bottomY),
			newBoundCubicCurveTo(backControlX, midY, backControlX, midY, backX, topY),
			newBoundCubicCurveTo(backX, topControl2Y, frontX, topControl2Y, frontX, topFrontY),
			newBoundLineTo(frontControlX, midY),
			newBoundLineTo(frontX, bottomY)
		);
		fill.setStroke(Color.TRANSPARENT);
		fill.setFill(Color.gray(0.95));
		
		final Path outline1 = new Path(
			newBoundMoveTo(foldX, fold1Y),
			newBoundLineTo(frontControlX, midY),
			newBoundLineTo(frontX, bottomY),
			newBoundCubicCurveTo(frontX, bottomControlY, backX, bottomControlY, backX, bottomY),
			newBoundCubicCurveTo(backControlX, midY, backControlX, midY, backX, topY),
			newBoundCubicCurveTo(backX, topControl2Y, frontX, topControl2Y, frontX, topFrontY),
			newBoundLineTo(frontControlX, midY),
			newBoundLineTo(foldX, fold2Y)
		);
		outline1.setStroke(Color.gray(0.05));
		outline1.setStrokeWidth(5);
		outline1.setStrokeLineCap(StrokeLineCap.ROUND);
		
		final Path outline2 = new Path(
			newBoundMoveTo(backX, topY),
			newBoundCubicCurveTo(backX, topControl1Y, frontX, topControl1Y, frontX, topFrontY)
		);
		outline2.setStroke(Color.gray(0.05));
		outline2.setStrokeWidth(5);
		
		this.node = new Group(
			fill,
			outline1,
			outline2
		);
	}
	
	@Override
	public Node getNode() { return this.node; }
	
	@Override public Map<DoubleProperty, Double> getInitializingKeyValues(
		  Side side
		, Point2D footPoint
	) {
		final Map<DoubleProperty, Double> retval = new java.util.HashMap<>();
		retval.put(scaleXProp, (side == Side.LEFT ? -1.0 : 1.0));
		retval.put(moveXProp, footPoint.getX());
		retval.put(moveYProp, footPoint.getY());
		retval.put(indentProp, 0.0);
		return retval;
	}
	
	@Override
	public Point2D getSpellTarget(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(moveXProp) - rolloverKeyValues.get(scaleXProp) * this.width / 2,
			rolloverKeyValues.get(moveYProp) - this.height / 2
		);
	}
	
	@Override
	public double getCurrentXOffset(Map<DoubleProperty, Double> rolloverKeyValues) {
		return rolloverKeyValues.get(moveXProp);
	}
	
	private Point2D getSpellOrigin(Map<DoubleProperty, Double> rolloverKeyValues) {
		return new Point2D(
			rolloverKeyValues.get(moveXProp) - rolloverKeyValues.get(scaleXProp) * this.width / 2,
			rolloverKeyValues.get(moveYProp) - this.height * 1.5
		);
	}
	
	@Override
	public Animation getAttackAnimation(
		  Function<Point2D, Animation> spellAnimationFun
		, Map<DoubleProperty, Double> rolloverKeyValues
		, Point2D target
		, ConsecutiveAttackDescriptor consecutiveAttackDesc
		, Set<AttackModifier> attackerModifiers
		, boolean isFinisher
	) {
		final Animation spellAnimation = spellAnimationFun.apply(this.getSpellOrigin(rolloverKeyValues));
		
		return new SequentialTransition(
			new PauseTransition(Duration.millis(200)),
			spellAnimation
		);
	}
	
	@Override
	public Animation getHitAnimation(
		  Map<DoubleProperty, Double> rolloverKeyValues
		, Set<AttackModifier> attackerModifiers
		, Set<AttackModifier> defenderModifiers
		, boolean isFinisher
	) {
		final double startIndent = rolloverKeyValues.get(indentProp);
		final double midIndent = Math.max(startIndent, this.height * 0.2);
		final double endIndent = (isFinisher ? this.height * 0.3 : 0.0);
		final double facing = rolloverKeyValues.get(scaleXProp);
		final double startX = rolloverKeyValues.get(moveXProp);
		final double endX = startX + facing * 30;
		
		rolloverKeyValues.put(moveXProp, endX);
		rolloverKeyValues.put(indentProp, endIndent);
			
		return new Timeline(
			new KeyFrame(Duration.ZERO,
				new KeyValue(moveXProp, startX, Interpolator.LINEAR),
				new KeyValue(indentProp, startIndent, Interpolator.LINEAR)
			),
			new KeyFrame(Duration.millis(100),
				new KeyValue(indentProp, midIndent, Interpolator.LINEAR)
			),
			new KeyFrame(Duration.millis(300),
				new KeyValue(indentProp, midIndent, Interpolator.LINEAR)
			),
			new KeyFrame(Duration.millis(500),
				new KeyValue(moveXProp, endX, Interpolator.EASE_OUT),
				new KeyValue(indentProp, endIndent, Interpolator.EASE_BOTH)
			)
		);
	}
}
