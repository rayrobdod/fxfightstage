package name.rayrobdod.femp_fx_demo;

import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.background.Field;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Dark;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.PhysicalHit;
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
		
		final Dark leftSpell = new Dark();
		final PhysicalHit rightSpell = new PhysicalHit();
		
		final Node gameNode = new Group(
			  Field.buildGroup(WIDTH, HEIGHT)
			, leftNode
			, rightNode
			, leftSpell.getNode()
			, rightSpell.getNode()
		);
		
		final Pane gamePane = new Pane(gameNode);
		gamePane.prefWidthProperty().set(WIDTH);
		gamePane.prefHeightProperty().set(HEIGHT);
		
		final Animation shakeAnimation = shakeAnimation(4, gameNode);
		
		final Button playButton = new Button("Play");
		playButton.setOnAction(x ->
			new SequentialTransition(
				leftUnit.getAttackAnimation(leftSpell.getAnimation(shakeAnimation)),
				rightUnit.getAttackAnimation(rightSpell.getAnimation(shakeAnimation))
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
	
	private static Animation shakeAnimation(int strength, Node node) {
		final Duration time = Duration.millis(40);
		final TranslateTransition v1 = new TranslateTransition(time, node);
		final TranslateTransition v2 = new TranslateTransition(time.multiply(2), node);
		final TranslateTransition v3 = new TranslateTransition(time, node);
		
		v1.setByX(strength);
		v1.setByY(strength * -1);
		v2.setByX(strength * -2);
		v2.setByY(strength * 2);
		v3.setByX(strength);
		v3.setByY(strength * -1);
		
		return new SequentialTransition(v1, v2, v3);
	}
}
