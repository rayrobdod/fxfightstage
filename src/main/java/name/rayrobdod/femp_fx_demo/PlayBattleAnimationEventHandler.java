package name.rayrobdod.femp_fx_demo;

import java.util.Arrays;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.StackPane;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.background.Field;

/**
 * Upon activation, plays a BattleAnimation based on the parameters in the provided StackPane
 */
public final class PlayBattleAnimationEventHandler implements EventHandler<ActionEvent> {
	private final StackPane gamePane;
	private final Supplier<UnitAnimationGroup> leftUnit;
	private final Supplier<UnitAnimationGroup> rightUnit;
	private final Supplier<SpellAnimationGroup> leftSpell;
	private final Supplier<SpellAnimationGroup> rightSpell;
	
	public PlayBattleAnimationEventHandler(
		  StackPane gamePane
		, Supplier<UnitAnimationGroup> leftUnit
		, Supplier<UnitAnimationGroup> rightUnit
		, Supplier<SpellAnimationGroup> leftSpell
		, Supplier<SpellAnimationGroup> rightSpell
	) {
		this.gamePane = gamePane;
		this.leftUnit = leftUnit;
		this.rightUnit = rightUnit;
		this.leftSpell = leftSpell;
		this.rightSpell = rightSpell;
	}
	
	public void handle(ActionEvent e) {
		final BattleAnimation.NodeAnimationPair pair = BattleAnimation.buildAnimation(
			Field::buildGroup,
			new Dimension2D(gamePane.getWidth(), gamePane.getHeight()),
			240,
			new BattleAnimation.AggregateSideParams(
				leftUnit.get(), leftSpell.get(), 60, 60
			),
			new BattleAnimation.AggregateSideParams(
				rightUnit.get(), rightSpell.get(), 60, 60
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
