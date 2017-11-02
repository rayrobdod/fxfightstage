package name.rayrobdod.femp_fx_demo;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;

import name.rayrobdod.femp_fx_demo.images.background.Field;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.SwordGuy;


public final class Main extends Application {
	
	private static final int WIDTH = 480;
	private static final int HEIGHT = 320;
	
	
	@Override
	public void start(Stage stage) {
		
		final SwordGuy leftUnit = new SwordGuy();
		final Node leftNode = leftUnit.getNode();
		leftNode.relocate(100, 120);
		leftNode.setScaleX(-1);
		
		final Node rightNode = new SwordGuy().getNode();
		rightNode.relocate(230, 120);
		
		final Pane gamePane = new Pane(
			  Field.buildGroup(WIDTH, HEIGHT)
			, leftNode
			, rightNode
		);
		gamePane.prefWidthProperty().set(WIDTH);
		gamePane.prefHeightProperty().set(HEIGHT);
		
		final Button playButton = new Button("Play");
		playButton.setOnAction(x -> leftUnit.getAttackAnimation().playFromStart());
		
		final BorderPane mainPane = new BorderPane();
		mainPane.setTop(playButton);
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
