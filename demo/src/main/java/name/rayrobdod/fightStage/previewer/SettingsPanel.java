package name.rayrobdod.fightStage.previewer;

import java.util.List;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

public final class SettingsPanel {
	
	private final GridPane node;
	
	public SettingsPanel(StackPane gamePane) {
		
		final Label labelUnit = new Label("Unit Animation");
		labelUnit.setPadding(new javafx.geometry.Insets(4));
		final ChoiceBox<NameSupplierPair<UnitAnimationGroup>> leftUnit = createNspChoicebox(unitOptions());
		final ChoiceBox<NameSupplierPair<UnitAnimationGroup>> rightUnit = createNspChoicebox(unitOptions());
		
		final Label labelSpell = new Label("Spell Animation");
		labelSpell.setPadding(new javafx.geometry.Insets(4));
		final ChoiceBox<NameSupplierPair<SpellAnimationGroup>> leftSpell = createNspChoicebox(spellOptions());
		final ChoiceBox<NameSupplierPair<SpellAnimationGroup>> rightSpell = createNspChoicebox(spellOptions());
		
		final Label labelDistance = new Label("Distance (px)");
		labelDistance.setPadding(new javafx.geometry.Insets(4));
		final Slider distance = new Slider(100, 600, 100);
		distance.setMajorTickUnit(100);
		distance.setBlockIncrement(100);
		distance.setShowTickMarks(true);
		
		final Label labelHp = new Label("HP");
		labelHp.setPadding(new javafx.geometry.Insets(4));
		final Spinner<Integer> leftCurrentHp = new Spinner<>(1, 99, 40);
		leftCurrentHp.setMaxWidth(1d/0d);
		leftCurrentHp.setEditable(true);
		leftCurrentHp.getEditor().setPrefColumnCount(4);
		final Spinner<Integer> leftMaximumHp = new Spinner<>(1, 99, 60);
		leftMaximumHp.setMaxWidth(1d/0d);
		leftMaximumHp.setEditable(true);
		leftMaximumHp.getEditor().setPrefColumnCount(4);
		final Spinner<Integer> rightCurrentHp = new Spinner<>(1, 99, 35);
		rightCurrentHp.setMaxWidth(1d/0d);
		rightCurrentHp.setEditable(true);
		rightCurrentHp.getEditor().setPrefColumnCount(4);
		final Spinner<Integer> rightMaximumHp = new Spinner<>(1, 99, 35);
		rightMaximumHp.setMaxWidth(1d/0d);
		rightMaximumHp.setEditable(true);
		rightMaximumHp.getEditor().setPrefColumnCount(4);
		final HBox leftHp = new HBox(3, leftCurrentHp, new Text("/"), leftMaximumHp);
		final HBox rightHp = new HBox(3, rightCurrentHp, new Text("/"), rightMaximumHp);
		
		
		Button playButton = new Button("Play");
		playButton.setMaxWidth(1d/0d);
		playButton.setOnAction(
			new PlayBattleAnimationEventHandler(
				  gamePane
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
		GridPane.setFillWidth(playButton, true);
		GridPane.setHgrow(leftUnit, Priority.ALWAYS);
		GridPane.setHgrow(rightUnit, Priority.ALWAYS);
		GridPane.setHgrow(playButton, Priority.ALWAYS);
		GridPane.setHalignment(playButton, HPos.CENTER);
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
		this.node.add(playButton, 0, 16, GridPane.REMAINING, 1);
	}
	
	public Node getNode() { return this.node; }
	
	
	
	private static <E> ChoiceBox<NameSupplierPair<E>> createNspChoicebox(List<NameSupplierPair<E>> options) {
		ChoiceBox<NameSupplierPair<E>> retval = new ChoiceBox<>();
		retval.getItems().addAll(options);
		retval.setValue(retval.getItems().get(0));
		retval.setConverter(new NameSupplierPairStringConverter<>());
		retval.setMaxWidth(1d/0d);
		return retval;
	}
	
	
	private static List<NameSupplierPair<UnitAnimationGroup>> unitOptions() {
		return UnitAnimationGroups.getAll();
	}
	
	private static List<NameSupplierPair<SpellAnimationGroup>> spellOptions() {
		return SpellAnimationGroups.getAll();
	}
	
	private static final class NameSupplierPairStringConverter<E> extends javafx.util.StringConverter<NameSupplierPair<E>> {
		public String toString(NameSupplierPair<E> xx) {return (null == xx ? "null" : xx.displayName);}
		public NameSupplierPair<E> fromString(String xx) {return null;}
	}
}
