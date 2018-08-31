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

import java.util.function.Function;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import name.rayrobdod.fightStage.Strike;

final class StrikeListEditor extends Control {
	private final ReadOnlyListWrapper<Strike> value;
	public final ReadOnlyListProperty<Strike> valueProperty() {return this.value.getReadOnlyProperty();}
	
	public StrikeListEditor() {
		this.value = new ReadOnlyListWrapper<>(this, "value", FXCollections.observableArrayList());
		this.setFocusTraversable(false);
	}
	
	protected Skin<?> createDefaultSkin() {return new DefaultSkin(this);}
	
	private static final class DefaultSkin implements Skin<StrikeListEditor> {
		private GridPane node;
		private StrikeListEditor skinnable;
		
		public DefaultSkin(StrikeListEditor skinnable) {
			this.skinnable = skinnable;
			this.node = new GridPane();
			
			final HBox strikes = new HBox();
			strikes.setSpacing(3);
			setToFillWidth(strikes);
			setToFillHeight(strikes);
			
			final Button addStrike = new Button();
			addStrike.setText("+");
			setToFillHeight(addStrike);
			addStrike.setOnAction((ev) -> {
				final int myIndex = skinnable.value.size();
				final StrikeCell cell = new StrikeCell();
				strikes.getChildren().add(cell);
				skinnable.value.add(cell.valueProperty().get());
				cell.valueProperty().addListener((ev2, from, to) ->
					skinnable.value.set(myIndex, to)
				);
			});
			
			node.add(strikes, 0, 0);
			node.add(addStrike, 1, 0);
		}
		
		public void dispose() {
			this.skinnable = null;
			this.node = null;
		}
		public Node getNode() {return this.node;}
		public StrikeListEditor getSkinnable() {return this.skinnable;}
	}
	
	
	private static <A,Z> ObjectBinding<Z> objectToObjectMapping(ObservableObjectValue<A> src, Function<A,Z> mapping, Z ifNull) {
		return new ObjectBinding<Z>() {
			{
				super.bind(src);
			}
			
			@Override
			protected Z computeValue() {
				A a = src.get();
				if (a == null) {
					return ifNull;
				} else {
					return mapping.apply(src.get());
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
}
