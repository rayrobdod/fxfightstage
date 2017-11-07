package name.rayrobdod.femp_fx_demo;

import java.util.Arrays;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

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
	
	private static final Dimension2D SIZE = new Dimension2D(480, 320);
	
	@Override
	public void start(Stage stage) {
		
		final SpellAnimationGroup leftSpell2 = new Dark();
		final SpellAnimationGroup leftSpell = new Lazor(Color.RED);
		final SpellAnimationGroup leftSpell3 = new Arrow();
		final SpellAnimationGroup rightSpell2 = new PhysicalHit();
		final SpellAnimationGroup rightSpell = new Lazor(Color.BLUE);
		final SpellAnimationGroup rightSpell3 = new Arrow();
		final UnitAnimationGroup leftUnit = new MageGuy();
		final UnitAnimationGroup rightUnit2 = new SwordGuy();
		final UnitAnimationGroup rightUnit = new BowGuy();
		
		final StackPane gamePane = new StackPane();
		gamePane.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.BLACK, null, null)));
		gamePane.getChildren().add(
			new Rectangle(0, 0, SIZE.getWidth(), SIZE.getHeight())
		);
		
		final Button playButton = new Button("Play");
		playButton.setMaxWidth(1d/0d);
		playButton.setOnAction(
			new PlayBattleAnimationEventHandler(
				  gamePane
				, leftUnit
				, rightUnit
				, leftSpell
				, rightSpell
			)
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
	
	
	private static final class PlayBattleAnimationEventHandler implements EventHandler<ActionEvent> {
		private final StackPane gamePane;
		private final UnitAnimationGroup leftUnit;
		private final UnitAnimationGroup rightUnit;
		private final SpellAnimationGroup leftSpell;
		private final SpellAnimationGroup rightSpell;
		
		public PlayBattleAnimationEventHandler(
			  StackPane gamePane
			, UnitAnimationGroup leftUnit
			, UnitAnimationGroup rightUnit
			, SpellAnimationGroup leftSpell
			, SpellAnimationGroup rightSpell
		) {
			this.gamePane = gamePane;
			this.leftUnit = leftUnit;
			this.rightUnit = rightUnit;
			this.leftSpell = leftSpell;
			this.rightSpell = rightSpell;
		}
		
		public void handle(ActionEvent e) {
			final BattleAnimation.NodeAnimationPair pair = BattleAnimation.buildAnimation(
				Field.buildGroup(SIZE.getWidth(), SIZE.getHeight()),
				SIZE,
				SIZE.getWidth() / 2,
				new BattleAnimation.AggregateSideParams(
					leftUnit, leftSpell, 60, 60
				),
				new BattleAnimation.AggregateSideParams(
					rightUnit, rightSpell, 60, 60
				),
				Arrays.asList(
					new BattleAnimation.Strike(BattleAnimation.Side.RIGHT, 20, 0),
					new BattleAnimation.Strike(BattleAnimation.Side.LEFT, 15, 0),
					new BattleAnimation.Strike(BattleAnimation.Side.LEFT, 15, 0),
					new BattleAnimation.Strike(BattleAnimation.Side.RIGHT, 20, 10)
				)
			);
			
			gamePane.getChildren().add(pair.node);
			pair.animation.setOnFinished(x -> gamePane.getChildren().remove(pair.node));
			pair.animation.playFromStart();
		}
	}
}
