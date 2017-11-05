package name.rayrobdod.femp_fx_demo;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public final class HealthBar {
	
	private final IntegerProperty maximumHealth;
	private final IntegerProperty currentHealth;
	private final BorderPane node;
	
	/**
	 * @param labelPosition if HPos.LEFT, the text label will be on the left side of the notches;
			else if HPos.RIGHT, the text label will be on the right side of the notches;
			else there will be no text label.
	 * @param currentHealth the initial value of the currentHealth property
	 * @param maxHealth the initial value of the maximumHealth property
	 */
	public HealthBar(HPos labelPosition, int currentHealth, int maxHealth) {
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
			notch.setWidth(3);
			notch.setHeight(14);
			notches.add(notch, (i - 1) % 40, (i - 1) / 40);
		}
		notches.setHgap(1);
		notches.setVgap(2);
		
		final Label label = new Label();
		label.textProperty().bind(this.currentHealth.asString());
		label.setPadding(new javafx.geometry.Insets(6));
		
		this.node = new BorderPane();
		this.node.setCenter(notches);
		if (labelPosition == HPos.LEFT) {
			this.node.setLeft(label);
		}
		if (labelPosition == HPos.RIGHT) {
			this.node.setRight(label);
		}
		BorderPane.setAlignment(label, Pos.CENTER);
	}
	
	public Node getNode() { return this.node; }
	
	public IntegerProperty maximumHealthProperty() { return this.maximumHealth; }
	public IntegerProperty currentHealthProperty() { return this.currentHealth; }
	
}
