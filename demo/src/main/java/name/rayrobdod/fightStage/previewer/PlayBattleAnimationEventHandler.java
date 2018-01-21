package name.rayrobdod.fightStage.previewer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import name.rayrobdod.fightStage.AggregateSideParams;
import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.BattleAnimation;
import name.rayrobdod.fightStage.NodeAnimationPair;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.Strike;
import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.background.Field;

/**
 * Upon activation, plays a BattleAnimation based on the parameters in the provided StackPane
 */
public final class PlayBattleAnimationEventHandler implements EventHandler<ActionEvent> {
	private final StackPane gamePane;
	private final List<Animation> currentAnimations;
	private final Supplier<UnitAnimationGroup> leftUnit;
	private final Supplier<UnitAnimationGroup> rightUnit;
	private final Supplier<SpellAnimationGroup> leftSpell;
	private final Supplier<SpellAnimationGroup> rightSpell;
	private final IntSupplier leftStartingHp;
	private final IntSupplier rightStartingHp;
	private final IntSupplier leftMaximumHp;
	private final IntSupplier rightMaximumHp;
	private final DoubleSupplier distance;
	
	public PlayBattleAnimationEventHandler(
		  StackPane gamePane
		, List<Animation> currentAnimations
		, Supplier<UnitAnimationGroup> leftUnit
		, Supplier<UnitAnimationGroup> rightUnit
		, Supplier<SpellAnimationGroup> leftSpell
		, Supplier<SpellAnimationGroup> rightSpell
		, IntSupplier leftStartingHp
		, IntSupplier rightStartingHp
		, IntSupplier leftMaximumHp
		, IntSupplier rightMaximumHp
		, DoubleSupplier distance
	) {
		this.gamePane = gamePane;
		this.currentAnimations = currentAnimations;
		this.leftUnit = leftUnit;
		this.rightUnit = rightUnit;
		this.leftSpell = leftSpell;
		this.rightSpell = rightSpell;
		this.leftStartingHp = leftStartingHp;
		this.rightStartingHp = rightStartingHp;
		this.leftMaximumHp = leftMaximumHp;
		this.rightMaximumHp = rightMaximumHp;
		this.distance = distance;
	}
	
	public void handle(ActionEvent e) {
		Set<AttackModifier> oneMods = new HashSet<>();
		oneMods.add(new FakeAttackModifier("Modifier"));
		Set<AttackModifier> threeMods = new HashSet<>();
		threeMods.add(new FakeAttackModifier("Modifier 1"));
		threeMods.add(new FakeAttackModifier("Modifier 2"));
		threeMods.add(new FakeAttackModifier("Modifier 3"));
		
		final NodeAnimationPair pair = BattleAnimation.buildAnimation(
			Field::buildGroup,
			new Dimension2D(gamePane.getWidth(), gamePane.getHeight()),
			this.distance.getAsDouble(),
			new AggregateSideParams(
				leftUnit.get(), leftSpell.get(), Color.RED.darker(),
				"Garnet", "Iron Thingy", new Circle(10),
				leftMaximumHp.getAsInt(), leftStartingHp.getAsInt()
			),
			new AggregateSideParams(
				rightUnit.get(), rightSpell.get(), Color.BLUE.darker(),
				"ABCDEFGHIJKL", "ABCDEFGHIJKLMNOP", new Circle(10),
				rightMaximumHp.getAsInt(), rightStartingHp.getAsInt()
			),
			Arrays.asList(
				new Strike(Side.RIGHT, 20, 0, oneMods, Collections.emptySet()),
				new Strike(Side.LEFT, 15, 0, Collections.emptySet(), Collections.emptySet()),
				new Strike(Side.LEFT, 15, 0, threeMods, threeMods),
				new Strike(Side.RIGHT, 20, 10, Collections.emptySet(), Collections.emptySet())
			)
		);
		
		gamePane.getChildren().add(pair.node);
		currentAnimations.add(pair.animation);
		pair.animation.setOnFinished(cleanUpPair(pair));
		pair.animation.playFromStart();
	}
	
	private EventHandler<ActionEvent> cleanUpPair(final NodeAnimationPair pair) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ignored) {
				gamePane.getChildren().remove(pair.node);
				currentAnimations.remove(pair.animation);
			}
		};
	}
}
