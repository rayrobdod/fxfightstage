package name.rayrobdod.femp_fx_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;


public final class BattleAnimation {
	private BattleAnimation() {}
	
	private static final Duration pauseDuration = Duration.millis(1000);
	private static final double distanceExtendPastPoint = 75;
	private static final double distanceFootBelowHorizon = 50;
	
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
	
	public static enum Distance {MELEE, RANGE, SIEGE;}
	
	public static final class Strike {
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
	
	public static final class NodeAnimationPair {
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
			-verticalDistance / 2,
			distanceFootBelowHorizon
		);
		final Point2D rightFootTarget = new Point2D(
			verticalDistance / 2,
			distanceFootBelowHorizon
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
		
		
		final Translate screenShakeTranslate = new Translate();
		final Translate centerTranslate = new Translate();
		final Translate panTranslate = new Translate();
		final Scale magnifyTransform = new Scale();
		
		final Node gameNode = new Group(
			  backgroundNode.apply(gamePanelSize)
			, left.spell.getBackground()
			, right.spell.getBackground()
			, left.unit.getNode()
			, right.unit.getNode()
			, left.spell.getForeground()
			, right.spell.getForeground()
		);
		gameNode.getTransforms().add(magnifyTransform);
		gameNode.getTransforms().add(screenShakeTranslate);
		gameNode.getTransforms().add(centerTranslate);
		gameNode.getTransforms().add(panTranslate);
		
		final Pane gamePane = new Pane(gameNode);
		final Rectangle gamePaneClip = new Rectangle();
		final DoubleBinding magnifyBinding = new MagnificationBinding(gamePane.widthProperty(), gamePane.heightProperty());
		gamePaneClip.heightProperty().bind(gamePane.heightProperty());
		gamePaneClip.widthProperty().bind(gamePane.widthProperty());
		gamePane.setClip(gamePaneClip);
		centerTranslate.xProperty().bind(gamePane.widthProperty().divide(2));
		centerTranslate.yProperty().bind(gamePane.heightProperty().multiply(2d/3d));
		magnifyTransform.xProperty().bind(magnifyBinding);
		magnifyTransform.yProperty().bind(magnifyBinding);
		magnifyTransform.pivotXProperty().bind(centerTranslate.xProperty());
		magnifyTransform.pivotYProperty().bind(centerTranslate.yProperty());
		
		
		final BorderPane retval_1 = new BorderPane();
		retval_1.setCenter(gamePane);
		retval_1.setBottom(healthbars);
		
		////////// The animation construction
		
		final Animation shakeAnimation = shakeAnimation(4, screenShakeTranslate);
		
		final ArrayList<Animation> animationParts = new ArrayList<>(strikes.size());
		animationParts.add(new PauseTransition(pauseDuration));
		
		int leftCurrentHitpoints = left.initialCurrentHitpoints;
		int rightCurrentHitpoints = right.initialCurrentHitpoints;
		double currentPan = 0;
		final double leftPan = Math.max(0,
			(verticalDistance / 2 + distanceExtendPastPoint) -
			(gamePanelSize.getWidth() / MagnificationBinding.compute(gamePanelSize.getWidth(), gamePanelSize.getHeight()) / 2)
		);
		final double rightPan = -leftPan;
		
		for (Strike strike : strikes) {
			switch (strike.attacker) {
				case LEFT: {
					final int leftNewHp = leftCurrentHitpoints + strike.drain;
					final int rightNewHp = rightCurrentHitpoints - strike.damage;
					
					Point2D target = right.unit.getNode().localToParent(right.unit.getSpellTarget());
					Point2D origin = left.unit.getNode().localToParent(left.unit.getSpellOrigin());
					
					animationParts.add(
						new SimpleDoubleTransition(
							Duration.millis(Math.abs(currentPan - leftPan)),
							panTranslate.xProperty(),
							currentPan,
							leftPan
						)
					);
					animationParts.add(
						left.unit.getAttackAnimation(
							left.spell.getAnimation(
								origin,
								target,
								new SimpleDoubleTransition(
									Duration.millis(Math.abs(rightPan - leftPan)),
									panTranslate.xProperty(),
									leftPan,
									rightPan
								),
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
					currentPan = rightPan;
					break;
				}
				case RIGHT: {
					final int leftNewHp = leftCurrentHitpoints - strike.damage;
					final int rightNewHp = rightCurrentHitpoints + strike.drain;
					
					Point2D target = left.unit.getNode().localToParent(left.unit.getSpellTarget());
					Point2D origin = right.unit.getNode().localToParent(right.unit.getSpellOrigin());
					
					animationParts.add(
						new SimpleDoubleTransition(
							Duration.millis(Math.abs(currentPan - rightPan)),
							panTranslate.xProperty(),
							currentPan,
							rightPan
						)
					);
					animationParts.add(
						right.unit.getAttackAnimation(
							right.spell.getAnimation(
								origin,
								target,
								new SimpleDoubleTransition(
									Duration.millis(Math.abs(leftPan - rightPan)),
									panTranslate.xProperty(),
									rightPan,
									leftPan
								),
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
					currentPan = leftPan;
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
	
	private static Animation shakeAnimation(int strength, Translate translate) {
		final Duration time = Duration.millis(40);
		
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(translate.xProperty(), 0, Interpolator.LINEAR),
			new KeyValue(translate.yProperty(), 0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(time,
			new KeyValue(translate.xProperty(), strength, Interpolator.LINEAR),
			new KeyValue(translate.yProperty(), -strength, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(time.multiply(3),
			new KeyValue(translate.xProperty(), -strength, Interpolator.LINEAR),
			new KeyValue(translate.yProperty(), strength, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(time.multiply(4),
			new KeyValue(translate.xProperty(), 0, Interpolator.LINEAR),
			new KeyValue(translate.yProperty(), 0, Interpolator.LINEAR)
		));
		
		return timeline;
	}
	
	private static final class MagnificationBinding extends DoubleBinding {
		private static final Dimension2D singleMagnificationSize = new Dimension2D(320, 240);
		private final ObservableDoubleValue containerWidth;
		private final ObservableDoubleValue containerHeight;
		
		public MagnificationBinding(ObservableDoubleValue containerWidth, ObservableDoubleValue containerHeight) {
			this.containerWidth = containerWidth;
			this.containerHeight = containerHeight;
			super.bind(containerWidth);
			super.bind(containerHeight);
		}
		
		@Override
		protected double computeValue() {
			return MagnificationBinding.compute(containerWidth.get(), containerHeight.get());
		}
		
		public static double compute(double width, double height) {
			return Math.max(1, (int) Math.min(
				width / singleMagnificationSize.getWidth(),
				height / singleMagnificationSize.getHeight()
			));
		}
	}
}
