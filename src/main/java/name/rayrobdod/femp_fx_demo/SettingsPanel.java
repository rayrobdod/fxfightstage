package name.rayrobdod.femp_fx_demo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Arrow;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Lazor;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.PhysicalHit;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.BowGuy;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.MageGuy;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.SwordGuy;

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
		
		Button playButton = new Button("Play");
		playButton.setMaxWidth(1d/0d);
		playButton.setOnAction(
			new PlayBattleAnimationEventHandler(
				  gamePane
				, () -> leftUnit.getValue().supplier.get()
				, () -> rightUnit.getValue().supplier.get()
				, () -> leftSpell.getValue().supplier.get()
				, () -> rightSpell.getValue().supplier.get()
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
		
		this.node = new GridPane();
		this.node.add(labelUnit, 0, 1);
		this.node.add(leftUnit, 1, 1);
		this.node.add(rightUnit, 2, 1);
		this.node.add(labelSpell, 0, 2);
		this.node.add(leftSpell, 1, 2);
		this.node.add(rightSpell, 2, 2);
		this.node.add(labelDistance, 0, 3);
		this.node.add(distance, 1, 3, GridPane.REMAINING, 1);
		this.node.add(playButton, 0, 4, GridPane.REMAINING, 1);
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
		return Arrays.asList(
			  new NameSupplierPair<>("SwordGuy", () -> new SwordGuy())
			, new NameSupplierPair<>("MageGuy", () -> new MageGuy())
			, new NameSupplierPair<>("BowGuy", () -> new BowGuy())
		);
	}
	
	private static List<NameSupplierPair<SpellAnimationGroup>> spellOptions() {
		return Arrays.asList(
			  new NameSupplierPair<>("Physical Hit", () -> new PhysicalHit())
			, new NameSupplierPair<>("Arrow", () -> new Arrow())
			, new NameSupplierPair<>("Dark", new FxmlSpellSupplier("images/battle_spell_anim/dark.fxml"))
			, new NameSupplierPair<>("Lazor (Blue)", () -> new Lazor(Color.BLUE))
			, new NameSupplierPair<>("Lazor (Red)", () -> new Lazor(Color.RED))
		);
	}
	
	private static final class NameSupplierPair<E> {
		public final String displayName;
		public final Supplier<E> supplier;
		
		public NameSupplierPair(
			String displayName,
			Supplier<E> supplier
		) {
			this.displayName = displayName;
			this.supplier = supplier;
		}
	}
	
	private static final class NameSupplierPairStringConverter<E> extends javafx.util.StringConverter<NameSupplierPair<E>> {
		public String toString(NameSupplierPair<E> xx) {return (null == xx ? "null" : xx.displayName);}
		public NameSupplierPair<E> fromString(String xx) {return null;}
	}
	
	private static final class FxmlSpellSupplier implements Supplier<SpellAnimationGroup> {
		private final String path;
		public FxmlSpellSupplier(String path) {
			this.path = path;
		}
		public SpellAnimationGroup get() {
			try {
				final java.net.URL url = SettingsPanel.class.getResource(this.path);
				final Object obj = javafx.fxml.FXMLLoader.load(url);
				return (SpellAnimationGroup) obj;
			} catch (java.io.IOException e) {
				throw new AssertionError("Failed to read file " + this.path, e);
			}
		}
	}
}
