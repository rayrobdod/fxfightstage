package name.rayrobdod.femp_fx_demo;

import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public final class Main extends Application {
	
	private static final Dimension2D SIZE = new Dimension2D(480, 320);
	
	@Override
	public void start(Stage stage) {
		
		final StackPane gamePane = new StackPane();
		gamePane.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.BLACK, null, null)));
		gamePane.getChildren().add(
			new Rectangle(0, 0, SIZE.getWidth(), SIZE.getHeight())
		);
		
		final SettingsPanel settings = new SettingsPanel(gamePane);
		
		
		final BorderPane mainPane = new BorderPane();
		mainPane.setTop(settings.getNode());
		mainPane.setCenter(gamePane);
		
		final Scene mainScene = new Scene(mainPane);
		
		stage.setTitle("Battle Animation Demo");
		stage.setScene(mainScene);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
}
