package name.rayrobdod.fightStage;

import static javafx.scene.text.FontWeight.BOLD;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * A component that displays a unit's current and maximum health
 */
final class HealthBar {
	
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
		notches.setVgap(2);
		notches.setAlignment(withVCenter(labelPosition));
		
		final Label hpText = new Label();
		hpText.textProperty().bind(this.currentHealth.asString());
		hpText.setTextFill(Color.WHITE);
		hpText.setFont(Font.font("Sans", BOLD, 18));
		hpText.setPadding(new javafx.geometry.Insets(6));
		hpText.setPrefWidth(40);
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
		this.node.setPadding(new Insets(8, 3, 6, 3));
		this.node.setBackground(BattleAnimation.solidBackground(teamColor));
		this.node.setBorder(new javafx.scene.layout.Border(
			new javafx.scene.layout.BorderStroke(
				  Color.WHITE
				, javafx.scene.layout.BorderStrokeStyle.SOLID
				, javafx.scene.layout.CornerRadii.EMPTY
				, new javafx.scene.layout.BorderWidths(
					3,
					(labelPosition == HPos.RIGHT ? 0 : 1.5),
					0,
					(labelPosition == HPos.LEFT ? 0 : 1.5)
				  )
			)
		));
	}
	
	/**
	 * Returns the node associated with this component.
	 * The object returned has the same identity each time.
	 */
	public Node getNode() { return this.node; }
	
	public IntegerProperty maximumHealthProperty() { return this.maximumHealth; }
	public IntegerProperty currentHealthProperty() { return this.currentHealth; }
	
	private static Pos withVCenter(HPos hpos) {
		switch (hpos) {
			case LEFT: return Pos.CENTER_LEFT;
			case CENTER: return Pos.CENTER;
			case RIGHT: return Pos.CENTER_RIGHT;
		}
		return Pos.CENTER;
	}
}
