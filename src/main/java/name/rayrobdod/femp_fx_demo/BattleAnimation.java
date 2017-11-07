package name.rayrobdod.femp_fx_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;


public final class BattleAnimation {
	private BattleAnimation() {}
	
	private static final Duration pauseDuration = Duration.millis(1000);
	
	public static final class AggregateSideParams {
		public final UnitAnimationGroup unit;
		public final SpellAnimationGroup spell;
		public final int maximumHitpoints;
		public final int initialCurrentHitpoints;
		
		public AggregateSideParams(
			  UnitAnimationGroup unit
			, SpellAnimationGroup spell
			, int maximumHitpoints
			, int initialCurrentHitpoints
		){
			this.unit = unit;
			this.spell = spell;
			this.maximumHitpoints = maximumHitpoints;
			this.initialCurrentHitpoints = initialCurrentHitpoints;
		}
	}
	
	public static enum Side {LEFT, RIGHT;}
	
	public static class Strike {
		public final Side attacker;
		public final int damage;
		public final int drain;
		// public final ??? triggeredSkills;
		
		public Strike(
			  Side attacker
			, int damage
			, int drain
		) {
			this.attacker = attacker;
			this.damage = damage;
			this.drain = drain;
		}
	}
	
	public static class NodeAnimationPair {
		public final Node node;
		public final Animation animation;
		
		public NodeAnimationPair(
			  Node node
			, Animation animation
		) {
			this.node = node;
			this.animation = animation;
		}
	}
	
	public static NodeAnimationPair buildAnimation(
		Function<Dimension2D, Node> backgroundNode,
		Dimension2D containerSize,
		double verticalDistance,
		AggregateSideParams left,
		AggregateSideParams right,
		List<Strike> strikes
	) {
		///////////// The node construction
		
		final HealthBar healthbarLeft = new HealthBar(HPos.LEFT, left.initialCurrentHitpoints, left.maximumHitpoints);
		final HealthBar healthbarRight = new HealthBar(HPos.RIGHT, right.initialCurrentHitpoints, right.maximumHitpoints);
		final HBox healthbars = new HBox(healthbarLeft.getNode(), healthbarRight.getNode());
		HBox.setHgrow(healthbarLeft.getNode(), Priority.ALWAYS);
		HBox.setHgrow(healthbarRight.getNode(), Priority.ALWAYS);
		healthbars.setFillHeight(true);
		healthbars.setPadding(new Insets(15, 3, 3, 3));
		healthbars.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.BURLYWOOD, null, null)));
		
		
		Dimension2D gamePanelSize = new Dimension2D(
			containerSize.getWidth(),
			containerSize.getHeight() - healthbars.prefHeight(containerSize.getWidth())
		);
		
		
		
		// find the locations that the units should be placed at
		final Point2D leftFootTarget = new Point2D(
			gamePanelSize.getWidth() / 2 - verticalDistance / 2,
			gamePanelSize.getHeight() * 7 / 8
		);
		final Point2D rightFootTarget = new Point2D(
			gamePanelSize.getWidth() / 2 + verticalDistance / 2,
			gamePanelSize.getHeight() * 7 / 8
		);
		
		// make the left unit face towards the right unit
		left.unit.getNode().setScaleX(-1);
		// the right unit already faces towards the left unit
		
		
		// place the units at the desired points
		left.unit.getNode().relocate(0, 0);
		right.unit.getNode().relocate(0, 0);
		
		Point2D leftOffset = leftFootTarget.subtract(
			left.unit.getNode().localToParent(left.unit.getFootPoint())
		);
		Point2D rightOffset = rightFootTarget.subtract(
			right.unit.getNode().localToParent(right.unit.getFootPoint())
		);
		
		left.unit.getNode().relocate(leftOffset.getX(), leftOffset.getY());
		right.unit.getNode().relocate(rightOffset.getX(), rightOffset.getY());
		
		
		final Node gameNode = new Group(
			  backgroundNode.apply(gamePanelSize)
			, left.unit.getNode()
			, right.unit.getNode()
			, left.spell.getNode()
			, right.spell.getNode()
		);
		final Pane gamePane = new Pane(gameNode);
		
		
		final BorderPane retval_1 = new BorderPane();
		retval_1.setCenter(gamePane);
		retval_1.setBottom(healthbars);
		
		////////// The animation construction
		
		final Animation shakeAnimation = shakeAnimation(4, gameNode);
		
		final ArrayList<Animation> animationParts = new ArrayList<>(strikes.size());
		animationParts.add(new PauseTransition(pauseDuration));
		
		int leftCurrentHitpoints = left.initialCurrentHitpoints;
		int rightCurrentHitpoints = right.initialCurrentHitpoints;
		for (Strike strike : strikes) {
			switch (strike.attacker) {
				case LEFT: {
					final int leftNewHp = leftCurrentHitpoints + strike.drain;
					final int rightNewHp = rightCurrentHitpoints - strike.damage;
					
					Point2D target = right.unit.getNode().localToParent(right.unit.getSpellTarget());
					Point2D origin = left.unit.getNode().localToParent(left.unit.getSpellOrigin());
					
					animationParts.add(
						left.unit.getAttackAnimation(
							left.spell.getAnimation(
								origin,
								target,
								new ParallelTransition(
									  shakeAnimation
									, healthbarAnimation(healthbarLeft, leftCurrentHitpoints, leftNewHp)
									, healthbarAnimation(healthbarRight, rightCurrentHitpoints, rightNewHp)
								)
							)
						)
					);
					
					leftCurrentHitpoints = leftNewHp;
					rightCurrentHitpoints = rightNewHp;
					break;
				}
				case RIGHT: {
					final int leftNewHp = leftCurrentHitpoints - strike.damage;
					final int rightNewHp = rightCurrentHitpoints + strike.drain;
					
					Point2D target = left.unit.getNode().localToParent(left.unit.getSpellTarget());
					Point2D origin = right.unit.getNode().localToParent(right.unit.getSpellOrigin());
					
					animationParts.add(
						right.unit.getAttackAnimation(
							right.spell.getAnimation(
								origin,
								target,
								new ParallelTransition(
									  shakeAnimation
									, healthbarAnimation(healthbarLeft, leftCurrentHitpoints, leftNewHp)
									, healthbarAnimation(healthbarRight, rightCurrentHitpoints, rightNewHp)
								)
							)
						)
					);
					
					leftCurrentHitpoints = leftNewHp;
					rightCurrentHitpoints = rightNewHp;
					break;
				}
			}
		}
		
		animationParts.add(new PauseTransition(pauseDuration));
		
		
		final SequentialTransition retval_2 = new SequentialTransition();
		retval_2.getChildren().addAll(animationParts);
	
		
		return new NodeAnimationPair(retval_1, retval_2);
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
