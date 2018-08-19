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
package name.rayrobdod.fightStage.previewer;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import name.rayrobdod.fightStage.AggregateSideParams;
import name.rayrobdod.fightStage.BattleAnimation;
import name.rayrobdod.fightStage.NodeAnimationPair;
import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.Strike;
import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.background.Field;

/**
 * Upon activation, plays a BattleAnimation based on the parameters in the provided StackPane
 */
final class PlayBattleAnimationEventHandler implements EventHandler<ActionEvent> {
	private final StackPane gamePane;
	private final ObjectProperty<Animation> currentAnimationProperty;
	private final Supplier<UnitAnimationGroup> leftUnit;
	private final Supplier<UnitAnimationGroup> rightUnit;
	private final Supplier<SpellAnimationGroup> leftSpell;
	private final Supplier<SpellAnimationGroup> rightSpell;
	private final IntSupplier leftStartingHp;
	private final IntSupplier rightStartingHp;
	private final IntSupplier leftMaximumHp;
	private final IntSupplier rightMaximumHp;
	private final Supplier<List<Strike>> strikes;
	private final DoubleSupplier distance;
	
	public PlayBattleAnimationEventHandler(
		  StackPane gamePane
		, ObjectProperty<Animation> currentAnimationProperty
		, Supplier<UnitAnimationGroup> leftUnit
		, Supplier<UnitAnimationGroup> rightUnit
		, Supplier<SpellAnimationGroup> leftSpell
		, Supplier<SpellAnimationGroup> rightSpell
		, IntSupplier leftStartingHp
		, IntSupplier rightStartingHp
		, IntSupplier leftMaximumHp
		, IntSupplier rightMaximumHp
		, Supplier<List<Strike>> strikes
		, DoubleSupplier distance
	) {
		this.gamePane = gamePane;
		this.currentAnimationProperty = currentAnimationProperty;
		this.leftUnit = leftUnit;
		this.rightUnit = rightUnit;
		this.leftSpell = leftSpell;
		this.rightSpell = rightSpell;
		this.leftStartingHp = leftStartingHp;
		this.rightStartingHp = rightStartingHp;
		this.leftMaximumHp = leftMaximumHp;
		this.rightMaximumHp = rightMaximumHp;
		this.strikes = strikes;
		this.distance = distance;
	}
	
	public void handle(ActionEvent e) {
		final NodeAnimationPair pair = BattleAnimation.buildAnimation(
			Field::buildGroup,
			new Dimension2D(gamePane.getWidth(), gamePane.getHeight()),
			this.distance.getAsDouble(),
			new AggregateSideParams(
				leftUnit.get(), leftSpell.get(), Color.RED.darker(),
				"Garnet", "Iron Thingy", new Circle(10),
				leftMaximumHp.getAsInt(), leftStartingHp.getAsInt()
			),
			new AggregateSideParams(
				rightUnit.get(), rightSpell.get(), Color.BLUE.darker(),
				"ABCDEFGHIJKL", "ABCDEFGHIJKLMNOP", new Circle(10),
				rightMaximumHp.getAsInt(), rightStartingHp.getAsInt()
			),
			strikes.get()
		);
		
		if (currentAnimationProperty.getValue() != null) {
			currentAnimationProperty.getValue().getOnFinished().handle(null);
		}
		gamePane.getChildren().add(pair.node);
		currentAnimationProperty.setValue(pair.animation);
		pair.animation.setOnFinished(cleanUpPair(pair));
		pair.animation.playFromStart();
	}
	
	private EventHandler<ActionEvent> cleanUpPair(final NodeAnimationPair pair) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ignored) {
				gamePane.getChildren().remove(pair.node);
				currentAnimationProperty.getValue().stop();
				currentAnimationProperty.setValue(null);
			}
		};
	}
}
