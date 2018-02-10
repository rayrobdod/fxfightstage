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

import static javafx.scene.text.FontWeight.BOLD;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * A component that displays a unit's current and maximum health
 */
final class HealthBar {
	
	private final DoubleProperty scale;
	private final IntegerProperty maximumHealth;
	private final IntegerProperty currentHealth;
	private final BorderPane node;
	
	/**
	 * @param labelPosition if HPos.LEFT, the text label will be on the left side of the notches;
			else if HPos.RIGHT, the text label will be on the right side of the notches;
			else there will be no text label.
	 * @param teamColor a color representing the unit's team
	 * @param currentHealth the initial value of the currentHealth property
	 * @param maxHealth the initial value of the maximumHealth property
	 */
	public HealthBar(
		  HPos labelPosition
		, Color teamColor
		, int currentHealth
		, int maxHealth
	) {
		this.scale = new SimpleDoubleProperty(1.0);
		this.maximumHealth = new SimpleIntegerProperty(maxHealth);
		this.currentHealth = new SimpleIntegerProperty(currentHealth);
		
		final GridPane notches = new GridPane();
		for (int i2 = 1; i2 <= 80; i2++) {
			final int i = i2;
			final Rectangle notch = new Rectangle();
			notch.setStroke(Color.BLACK);
			notch.fillProperty().bind(
				new ObjectBinding<Paint>() {
					{
						super.bind(HealthBar.this.currentHealth);
					}
					@Override
					protected Paint computeValue() {
						if (HealthBar.this.currentHealth.get() >= i) {
							return Color.LIME;
						} else {
							return Color.GREY;
						}
					}
				}
			);
			notch.visibleProperty().bind(this.maximumHealth.greaterThanOrEqualTo(i));
			notch.widthProperty().bind(this.scale.multiply(3));
			notch.heightProperty().bind(this.scale.multiply(14));
			notches.add(notch, (i - 1) % 40, (i - 1) / 40);
		}
		notches.vgapProperty().bind(this.scale.multiply(2));
		notches.setAlignment(withVCenter(labelPosition));
		
		final Label hpText = new Label();
		hpText.textProperty().bind(this.currentHealth.asString());
		hpText.setTextFill(Color.WHITE);
		hpText.fontProperty().bind(
			new ObjectBinding<Font>() {
				{
					super.bind(HealthBar.this.scale);
				}
				@Override
				protected Font computeValue() {
					return Font.font("Sans", BOLD, HealthBar.this.scale.get() * 18);
				}
			}
		);
		hpText.paddingProperty().bind(
			new InsetScaleBinding(new Insets(6), HealthBar.this.scale)
		);
		hpText.prefWidthProperty().bind(this.scale.multiply(40));
		hpText.setAlignment(Pos.CENTER);
		
		this.node = new BorderPane();
		this.node.setCenter(notches);
		if (labelPosition == HPos.LEFT) {
			this.node.setLeft(hpText);
		}
		if (labelPosition == HPos.RIGHT) {
			this.node.setRight(hpText);
		}
		BorderPane.setAlignment(hpText, Pos.CENTER);
		this.node.paddingProperty().bind(
			new InsetScaleBinding(new Insets(8, 3, 6, 3), HealthBar.this.scale)
		);
		this.node.setBackground(BattleAnimation.solidBackground(teamColor));
		this.node.borderProperty().bind(
			new ObjectBinding<Border>() {
				{
					super.bind(HealthBar.this.scale);
				}
				@Override
				protected Border computeValue() {
					final double scaleVal = HealthBar.this.scale.get();
					return new Border(
						new BorderStroke(
							  Color.WHITE
							, BorderStrokeStyle.SOLID
							, CornerRadii.EMPTY
							, new BorderWidths(
								scaleVal * 3,
								scaleVal * (labelPosition == HPos.RIGHT ? 0 : 1.5),
								scaleVal * 0,
								scaleVal * (labelPosition == HPos.LEFT ? 0 : 1.5)
							  )
						)
					);
				}
			}
		);
	}
	
	/**
	 * Returns the node associated with this component.
	 * The object returned has the same identity each time.
	 */
	public Node getNode() { return this.node; }
	
	/** The maximum health displayed by this component */
	public IntegerProperty maximumHealthProperty() { return this.maximumHealth; }
	/** The current health displayed by this component */
	public IntegerProperty currentHealthProperty() { return this.currentHealth; }
	/** A value that any internal related to size are multiplied by */
	public DoubleProperty scaleProperty() { return this.scale; }
	
	private static Pos withVCenter(HPos hpos) {
		switch (hpos) {
			case LEFT: return Pos.CENTER_LEFT;
			case CENTER: return Pos.CENTER;
			case RIGHT: return Pos.CENTER_RIGHT;
		}
		return Pos.CENTER;
	}
	
	private static class InsetScaleBinding extends ObjectBinding<Insets> {
		private final Insets base;
		private final ObservableDoubleValue scale;
		
		public InsetScaleBinding(Insets base, ObservableDoubleValue scale) {
			this.base = base;
			this.scale = scale;
			super.bind(scale);
		}
		
		@Override
		protected Insets computeValue() {
			final double scaleVal = this.scale.get();
			return new Insets(
				  scaleVal * base.getTop()
				, scaleVal * base.getRight()
				, scaleVal * base.getBottom()
				, scaleVal * base.getLeft()
			);
		}
	}
}
