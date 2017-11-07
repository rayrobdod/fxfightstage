package name.rayrobdod.femp_fx_demo;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.background.Field;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Arrow;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Dark;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.Lazor;
import name.rayrobdod.femp_fx_demo.images.battle_spell_anim.PhysicalHit;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.BowGuy;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.MageGuy;
import name.rayrobdod.femp_fx_demo.images.battle_unit_anim.SwordGuy;


public final class Main extends Application {
	
	private static final int WIDTH = 480;
	private static final int HEIGHT = 320;
	
	private static final Point2D leftFootTarget = new Point2D(WIDTH * 2 / 8, HEIGHT * 7 / 8);
	private static final Point2D rightFootTarget = new Point2D(WIDTH * 6 / 8, HEIGHT * 7 / 8);
	
	@Override
	public void start(Stage stage) {
		
		final SpellAnimationGroup leftSpell2 = new Dark();
		final SpellAnimationGroup leftSpell3 = new Lazor(Color.RED);
		final SpellAnimationGroup leftSpell = new Arrow();
		final SpellAnimationGroup rightSpell2 = new PhysicalHit();
		final SpellAnimationGroup rightSpell3 = new Lazor(Color.BLUE);
		final SpellAnimationGroup rightSpell = new Arrow();
		final UnitAnimationGroup leftUnit = new MageGuy();
		final UnitAnimationGroup rightUnit2 = new SwordGuy();
		final UnitAnimationGroup rightUnit = new BowGuy();
		
		// move left and right into position, facing each other and
		// located with their foot points at the foot targets
		{
			final Node leftNode = leftUnit.getNode();
			leftNode.setScaleX(-1);
			final Point2D relocateTo = leftFootTarget.subtract(
				leftNode.localToParent(leftUnit.getFootPoint())
			);
			leftNode.relocate(relocateTo.getX(), relocateTo.getY());
		}{
			final Node rightNode = rightUnit.getNode();
			final Point2D relocateTo = rightFootTarget.subtract(
				rightNode.localToParent(rightUnit.getFootPoint())
			);
			rightNode.relocate(relocateTo.getX(), relocateTo.getY());
		}
		
		final Node gameNode = new Group(
			  Field.buildGroup(WIDTH, HEIGHT)
			, leftUnit.getNode()
			, rightUnit.getNode()
			, leftSpell.getNode()
			, rightSpell.getNode()
		);
		
		final Pane gamePane = new Pane(gameNode);
		gamePane.prefWidthProperty().set(WIDTH);
		gamePane.prefHeightProperty().set(HEIGHT);
		
		final HealthBar healthbarLeft = new HealthBar(HPos.LEFT, 65, 65);
		final HealthBar healthbarRight = new HealthBar(HPos.RIGHT, 35, 35);
		final HBox healthbars = new HBox(healthbarLeft.getNode(), healthbarRight.getNode());
		HBox.setHgrow(healthbarLeft.getNode(), Priority.ALWAYS);
		HBox.setHgrow(healthbarRight.getNode(), Priority.ALWAYS);
		healthbars.setFillHeight(true);
		
		final Animation shakeAnimation = shakeAnimation(4, gameNode);
		final Animation healthbarLeftAnimation = healthbarAnimation(healthbarLeft, 65, 30);
		final Animation healthbarRightAnimation = healthbarAnimation(healthbarRight, 35, 25);
		
		final Button playButton = new Button("Play");
		playButton.setMaxWidth(1d/0d);
		playButton.setOnAction(x ->
			new SequentialTransition(
				leftUnit.getAttackAnimation(leftSpell.getAnimation(
					leftUnit.getNode().localToParent(leftUnit.getSpellOrigin()),
					rightUnit.getNode().localToParent(rightUnit.getSpellTarget()),
					new ParallelTransition(shakeAnimation, healthbarRightAnimation)
				)),
				rightUnit.getAttackAnimation(rightSpell.getAnimation(
					rightUnit.getNode().localToParent(rightUnit.getSpellOrigin()),
					leftUnit.getNode().localToParent(leftUnit.getSpellTarget()),
					new ParallelTransition(shakeAnimation, healthbarLeftAnimation)
				))
			).playFromStart()
		);
		
		
		final BorderPane mainPane = new BorderPane();
		mainPane.setTop(playButton);
		mainPane.setCenter(gamePane);
		mainPane.setBottom(healthbars);
		BorderPane.setMargin(healthbars, new Insets(15, 3, 3, 3));
		
		final Scene mainScene = new Scene(mainPane);
		
		stage.setTitle("Battle Animation Demo");
		stage.setScene(mainScene);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
	
	private static Animation healthbarAnimation(HealthBar hb, int from, int to) {
		final Duration timePerTick = Duration.millis(50);
		final Duration time = timePerTick.multiply(Math.abs(to - from));
		
		return new SimpleIntegerTransition(time, hb.currentHealthProperty(), from, to);
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
