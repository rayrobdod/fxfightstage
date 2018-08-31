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

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Polygon;

import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.Strike;

public final class StrikeCell extends Control {
	private static final double graphicWidth = 10;
	
	private final ReadOnlyObjectWrapper<Strike> value;
	public final ReadOnlyObjectProperty<Strike> valueProperty() {return this.value.getReadOnlyProperty();}
	
	public StrikeCell() {
		this.value = new ReadOnlyObjectWrapper<>(this, "value",
				new Strike(Side.LEFT, 20, 0, Collections.emptySet(), Collections.emptySet()));
		this.setFocusTraversable(false);
	}
	
	protected Skin<?> createDefaultSkin() {return new DefaultSkin(this);}
	
	private static final class DefaultSkin implements Skin<StrikeCell> {
		private GridPane node;
		private StrikeCell skinnable;
		
		public DefaultSkin(StrikeCell skinnable) {
			this.skinnable = skinnable;
			this.node = new GridPane();
			
			final ObservableObjectValue<Side> attacker; {
				final ToggleGroup sideToggles = new ToggleGroup();
				final ToggleButton left = sideToggleButton(Side.LEFT, sideToggles);
				final ToggleButton right = sideToggleButton(Side.RIGHT, sideToggles);
				
				this.node.add(left, 0, 0, 1, 1);
				this.node.add(right, 1, 0, 1, 1);
				
				if (skinnable.value.get().attacker == Side.RIGHT) {
					sideToggles.selectToggle(right);
				} else {
					sideToggles.selectToggle(left);
				}
				
				attacker = objectToObjectMapping(
					sideToggles.selectedToggleProperty(),
					(x) -> {
						if (x == left) {return Side.LEFT;}
						if (x == right) {return Side.RIGHT;}
						return null;
					}
				);
			}
			
			final IntegerProperty damage = new SimpleIntegerProperty();
			this.node.add(integerInput(damage, -50, 50, 15), 0, 1, 2, 1);
			
			final IntegerProperty drain = new SimpleIntegerProperty();
			this.node.add(integerInput(drain, -50, 50, 0), 0, 2, 2, 1);
			
			final ObjectProperty<Set<AttackModifier>> atkMod = new SimpleObjectProperty<Set<AttackModifier>>();
			this.node.add(attackModInput(atkMod), 0, 3, 2, 1);
			
			final ObjectProperty<Set<AttackModifier>> defMod = new SimpleObjectProperty<Set<AttackModifier>>();
			this.node.add(attackModInput(defMod), 0, 4, 2, 1);
			
			skinnable.value.bind(strike(attacker, damage, drain, atkMod, defMod));
		}
		
		public void dispose() {
			this.skinnable = null;
			this.node = null;
		}
		public Node getNode() {return this.node;}
		public StrikeCell getSkinnable() {return this.skinnable;}
		
		
		
		private static ToggleButton sideToggleButton(Side side, ToggleGroup toggleGroup) {
			final String text;
			final Node graphic;
			if (side == null) {
				text = "N/A";
				graphic = new Polygon(0,0,   0,graphicWidth,    graphicWidth,graphicWidth,   graphicWidth,0);
			} else {
				switch (side) {
					case LEFT:
						text = "LEFT";
						graphic = new Polygon(graphicWidth,0,     graphicWidth,graphicWidth,    0,graphicWidth/2);
						break;
					case RIGHT:
						text = "RIGHT";
						graphic = new Polygon(0,0,    0,graphicWidth,     graphicWidth,graphicWidth/2);
						break;
					default:
						text = "N/A";
						graphic = new Polygon(0,0,   0,graphicWidth,    graphicWidth,graphicWidth,   graphicWidth,0);
				}
			}
			
			final ToggleButton retval = new ToggleButton(text, graphic);
			retval.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			retval.setToggleGroup(toggleGroup);
			setToFillWidth(retval);
			setToFillHeight(retval);
			
			return retval;
		}
		private static Node integerInput(Property<Number> property, int min, int max, int initial) {
			Spinner<Integer> retval = new Spinner<>(new IntegerSpinnerValueFactory(min, max, initial));
			property.bind(retval.valueProperty());
			retval.setEditable(false);
			retval.getEditor().setPrefColumnCount(4);
			setToFillWidth(retval);
			setToFillHeight(retval);
			
			return retval;
		}
		private static Node attackModInput(Property<Set<AttackModifier>> property) {
			ObservableList<Set<AttackModifier>> values = FXCollections.observableArrayList();
			values.add(Collections.emptySet());
			values.add(set(new AttackModifier("Abscond")));
			values.add(set(new AttackModifier("Belay")));
			values.add(set(new AttackModifier("Abscond"), new AttackModifier("Belay")));
			
			SpinnerValueFactory<Set<AttackModifier>> valueFactory = new ListSpinnerValueFactory<>(values);
			valueFactory.setConverter(new javafx.util.StringConverter<Set<AttackModifier>>() {
				public Set<AttackModifier> fromString(String s) {return null;}
				public String toString(Set<AttackModifier> s) {
					return s.stream()
						.map(x -> x.displayName.substring(0, 1))
						.collect(Collectors.joining(",", "[", "]"));
				}
			});
			
			Spinner<Set<AttackModifier>> retval = new Spinner<>(valueFactory);
			property.bind(retval.valueProperty());
			retval.setEditable(false);
			retval.getEditor().setPrefColumnCount(4);
			setToFillWidth(retval);
			setToFillHeight(retval);
			
			return retval;
		}
	}
	
	private static <A,Z> ObjectBinding<Z> objectToObjectMapping(ObservableObjectValue<A> src, Function<A,Z> mapping) {
		return new ObjectBinding<Z>() {
			{
				super.bind(src);
			}
			
			@Override
			protected Z computeValue() {
				return mapping.apply(src.get());
			}
		};
	}
	
	private static ObjectBinding<Strike> strike(
		  ObservableObjectValue<Side> attacker
		, ObservableIntegerValue damage
		, ObservableIntegerValue drain
		, ObservableObjectValue<Set<AttackModifier>> atkMod
		, ObservableObjectValue<Set<AttackModifier>> defMod
	) {
		return new ObjectBinding<Strike>() {
			{
				super.bind(attacker);
				super.bind(damage);
				super.bind(drain);
				super.bind(atkMod);
				super.bind(defMod);
			}
			
			@Override
			protected Strike computeValue() {
				if (attacker == null) {
					return null;
				} else {
					return new Strike(
						attacker.get(),
						damage.get(),
						drain.get(),
						atkMod.get(),
						defMod.get()
					);
				}
			}
		};
	}
	
	private static void setToFillWidth(Region item) {
		item.setMaxWidth(java.lang.Double.MAX_VALUE);
		GridPane.setFillWidth(item, true);
		GridPane.setHgrow(item, Priority.ALWAYS);
	}
	private static void setToFillHeight(Region item) {
		item.setMaxHeight(java.lang.Double.MAX_VALUE);
		GridPane.setFillHeight(item, true);
		GridPane.setVgrow(item, Priority.ALWAYS);
	}
	
	private static <A> Set<A> set(A... items) {
		Set<A> retval = new java.util.HashSet<A>(items.length);
		for (A item : items) {retval.add(item);}
		return retval;
	}
}
