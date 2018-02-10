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

import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.previewer.spi.NameSupplierPair;
import name.rayrobdod.fightStage.previewer.spi.SpellAnimationGroups;
import name.rayrobdod.fightStage.previewer.spi.UnitAnimationGroups;

/**
 * A panel that allows a user to set battle animation properties
 */
final class SettingsPanel {
	
	private final GridPane node;
	private final ObjectProperty<Animation> currentAnimationProperty;
	
	public SettingsPanel(StackPane gamePane) {
		this.currentAnimationProperty = new SimpleObjectProperty<>();
		
		final Label labelUnit = new Label("Unit Animation");
		labelUnit.setPadding(new javafx.geometry.Insets(4));
		final ChoiceBox<NameSupplierPair<UnitAnimationGroup>> leftUnit = createNspChoicebox(UnitAnimationGroups.getAll());
		final ChoiceBox<NameSupplierPair<UnitAnimationGroup>> rightUnit = createNspChoicebox(UnitAnimationGroups.getAll());
		
		final Label labelSpell = new Label("Spell Animation");
		labelSpell.setPadding(new javafx.geometry.Insets(4));
		final ChoiceBox<NameSupplierPair<SpellAnimationGroup>> leftSpell = createNspChoicebox(SpellAnimationGroups.getAll());
		final ChoiceBox<NameSupplierPair<SpellAnimationGroup>> rightSpell = createNspChoicebox(SpellAnimationGroups.getAll());
		
		final Label labelDistance = new Label("Distance (px)");
		labelDistance.setPadding(new javafx.geometry.Insets(4));
		final Slider distance = new Slider(100, 600, 100);
		distance.setMajorTickUnit(100);
		distance.setBlockIncrement(100);
		distance.setShowTickMarks(true);
		
		final Label labelHp = new Label("HP");
		labelHp.setPadding(new javafx.geometry.Insets(4));
		final Spinner<Integer> leftCurrentHp = createHpSpinner(40);
		final Spinner<Integer> leftMaximumHp = createHpSpinner(60);
		final Spinner<Integer> rightCurrentHp = createHpSpinner(35);
		final Spinner<Integer> rightMaximumHp = createHpSpinner(35);
		final HBox leftHp = new HBox(3, leftCurrentHp, new Text("/"), leftMaximumHp);
		final HBox rightHp = new HBox(3, rightCurrentHp, new Text("/"), rightMaximumHp);
		
		MediaControlPanel mediaControlPanel = new MediaControlPanel(
			  this.currentAnimationProperty
			, new PlayBattleAnimationEventHandler(
				  gamePane
				, this.currentAnimationProperty
				, () -> leftUnit.getValue().supplier.get()
				, () -> rightUnit.getValue().supplier.get()
				, () -> leftSpell.getValue().supplier.get()
				, () -> rightSpell.getValue().supplier.get()
				, () -> leftCurrentHp.getValue()
				, () -> rightCurrentHp.getValue()
				, () -> leftMaximumHp.getValue()
				, () -> rightMaximumHp.getValue()
				, () -> distance.getValue()
			  )
		);
		
		GridPane.setFillWidth(leftUnit, true);
		GridPane.setFillWidth(rightUnit, true);
		GridPane.setHgrow(leftUnit, Priority.ALWAYS);
		GridPane.setHgrow(rightUnit, Priority.ALWAYS);
		HBox.setHgrow(leftCurrentHp, Priority.ALWAYS);
		HBox.setHgrow(leftMaximumHp, Priority.ALWAYS);
		HBox.setHgrow(rightCurrentHp, Priority.ALWAYS);
		HBox.setHgrow(rightMaximumHp, Priority.ALWAYS);
		
		this.node = new GridPane();
		this.node.add(labelUnit, 0, 1);
		this.node.add(leftUnit, 1, 1);
		this.node.add(rightUnit, 2, 1);
		this.node.add(labelSpell, 0, 2);
		this.node.add(leftSpell, 1, 2);
		this.node.add(rightSpell, 2, 2);
		this.node.add(labelHp, 0, 3);
		this.node.add(leftHp, 1, 3);
		this.node.add(rightHp, 2, 3);
		this.node.add(labelDistance, 0, 15);
		this.node.add(distance, 1, 15, GridPane.REMAINING, 1);
		this.node.add(mediaControlPanel.getNode(), 0, 16, GridPane.REMAINING, 1);
	}
	
	public Node getNode() { return this.node; }
	
	
	/** Creates a spinner that allows selection of a HP value */
	private static Spinner<Integer> createHpSpinner(int initialValue) {
		Spinner<Integer> retval = new Spinner<>(1, 99, initialValue);
		retval.setMaxWidth(1d/0d);
		retval.setEditable(true);
		retval.getEditor().setPrefColumnCount(4);
		return retval;
	}
	
	/** Creates a choicebox that allows selection of an &lt;E&gt; value */
	private static <E> ChoiceBox<NameSupplierPair<E>> createNspChoicebox(List<NameSupplierPair<E>> options) {
		ChoiceBox<NameSupplierPair<E>> retval = new ChoiceBox<>();
		retval.getItems().addAll(options);
		retval.setValue(retval.getItems().get(0));
		retval.setConverter(new NameSupplierPairStringConverter<>());
		retval.setMaxWidth(1d/0d);
		return retval;
	}
	
	private static final class NameSupplierPairStringConverter<E> extends javafx.util.StringConverter<NameSupplierPair<E>> {
		public String toString(NameSupplierPair<E> xx) {return (null == xx ? "null" : xx.displayName);}
		public NameSupplierPair<E> fromString(String xx) {return null;}
	}
}
