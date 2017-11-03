package name.rayrobdod.femp_fx_demo;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import name.rayrobdod.femp_fx_demo.images.background.Field;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Dark;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.MageGuy;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.SwordGuy;


public final class Main extends Application {
	
	private static final int WIDTH = 480;
	private static final int HEIGHT = 320;
	
	
	@Override
	public void start(Stage stage) {
		
		final MageGuy leftUnit = new MageGuy();
		final Node leftNode = leftUnit.getNode();
		leftNode.relocate(100, 120);
		leftNode.setScaleX(-1);
		
		final SwordGuy rightUnit = new SwordGuy();
		final Node rightNode = rightUnit.getNode();
		rightNode.relocate(230, 120);
		
		final Dark spell = new Dark();
		
		final Pane gamePane = new Pane(
			  Field.buildGroup(WIDTH, HEIGHT)
			, leftNode
			, rightNode
			, spell.getNode()
		);
		gamePane.prefWidthProperty().set(WIDTH);
		gamePane.prefHeightProperty().set(HEIGHT);
		
		final Button playButton = new Button("Play");
		playButton.setOnAction(x ->
			new javafx.animation.SequentialTransition(
				leftUnit.getAttackAnimation(spell.getAnimation()),
				rightUnit.getAttackAnimation()
			).playFromStart()
		);
		
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
