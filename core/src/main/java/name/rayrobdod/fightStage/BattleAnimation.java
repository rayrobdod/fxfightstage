package name.rayrobdod.fightStage;

import static javafx.scene.text.FontWeight.BOLD;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;


public final class BattleAnimation {
	private BattleAnimation() {}
	
	private static final Duration pauseDuration = Duration.millis(1000);
	private static final double distanceExtendPastPoint = 75;
	private static final double distanceFootBelowHorizon = 50;
	
	public static final class AggregateSideParams {
		public final UnitAnimationGroup unit;
		public final SpellAnimationGroup spell;
		public final Color teamColor;
		public final String unitName;
		public final String weaponName;
		public final Node weaponIcon;
		/** The unit's maximum hitpoints */
		public final int maximumHitpoints;
		/** The unit's starting current hitpoints */
		public final int initialCurrentHitpoints;
		
		public AggregateSideParams(
			  UnitAnimationGroup unit
			, SpellAnimationGroup spell
			, Color teamColor
			, String unitName
			, String weaponName
			, Node weaponIcon
			, int maximumHitpoints
			, int initialCurrentHitpoints
		){
			this.unit = unit;
			this.spell = spell;
			this.teamColor = teamColor;
			this.unitName = unitName;
			this.weaponName = weaponName;
			this.weaponIcon = weaponIcon;
			this.maximumHitpoints = maximumHitpoints;
			this.initialCurrentHitpoints = initialCurrentHitpoints;
		}
	}
	
	public static enum Side {LEFT, RIGHT;}
	
	/** a placeholder to represent a real class that this demo doesn't really care about */
	public static enum AttackModifier {CRITICAL, MISS, LUNA;}
	
	/** A description of one attack */
	public static final class Strike {
		/** Which unit is performing an attack */
		public final Side attacker;
		/** The damage dealt to the defender */
		public final int damage;
		/**
		 * The damage healed by the attacker.
		 * Probably can be negative for counter-attack damage.
		 */
		public final int drain;
		/** Skills triggered during this attack. Just for the sake of identity. */
		public final Set<AttackModifier> triggeredSkills;
		
		public Strike(
			  Side attacker
			, int damage
			, int drain
			, Set<AttackModifier> triggeredSkills
		) {
			this.attacker = attacker;
			this.damage = damage;
			this.drain = drain;
			this.triggeredSkills = triggeredSkills;
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
		
		final HealthBar healthbarLeft = new HealthBar(HPos.LEFT, left.teamColor, left.initialCurrentHitpoints, left.maximumHitpoints);
		final HealthBar healthbarRight = new HealthBar(HPos.RIGHT, right.teamColor, right.initialCurrentHitpoints, right.maximumHitpoints);
		final Label leftUnitName = unitNameLabel(left.unitName, left.teamColor, HPos.LEFT);
		final Label rightUnitName = unitNameLabel(right.unitName, right.teamColor, HPos.RIGHT);
		final Label leftWeaponName = weaponLabel(left.weaponName, left.weaponIcon, left.teamColor, HPos.LEFT);
		final Label rightWeaponName = weaponLabel(right.weaponName, right.weaponIcon, right.teamColor, HPos.RIGHT);
		
		
		final Dimension2D gamePanelSize = new Dimension2D(
			containerSize.getWidth(),
			containerSize.getHeight()
		);
		
		
		// make the left unit face towards the right unit
		left.unit.getNode().getTransforms().add(new Scale(-1, 1));
		// the right unit already faces towards the left unit
		
		
		// place the units at their starting location
		final Point2D initialUnitOffset = new Point2D(
			verticalDistance / 2,
			distanceFootBelowHorizon
		);
		final Translate initialUnitTransform = new Translate(initialUnitOffset.getX(), initialUnitOffset.getY());
		left.unit.getNode().getTransforms().add(initialUnitTransform);
		right.unit.getNode().getTransforms().add(initialUnitTransform);
		
		
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
		centerTranslate.yProperty().bind(gamePane.heightProperty().multiply(1d/2d));
		magnifyTransform.xProperty().bind(magnifyBinding);
		magnifyTransform.yProperty().bind(magnifyBinding);
		magnifyTransform.pivotXProperty().bind(centerTranslate.xProperty());
		magnifyTransform.pivotYProperty().bind(centerTranslate.yProperty());
		
		
		final GridPane bottomHud = new GridPane();
		bottomHud.addRow(0, leftWeaponName, rightWeaponName);
		bottomHud.addRow(1, healthbarLeft.getNode(), healthbarRight.getNode());
		GridPane.setHgrow(healthbarLeft.getNode(), Priority.ALWAYS);
		GridPane.setHgrow(healthbarRight.getNode(), Priority.ALWAYS);
		GridPane.setHalignment(leftWeaponName, HPos.LEFT);
		GridPane.setHalignment(rightWeaponName, HPos.RIGHT);
		
		final AnchorPane retval_1 = new AnchorPane();
		AnchorPane.setTopAnchor(gamePane, 0.0);
		AnchorPane.setLeftAnchor(gamePane, 0.0);
		AnchorPane.setRightAnchor(gamePane, 0.0);
		AnchorPane.setBottomAnchor(gamePane, 0.0);
		AnchorPane.setLeftAnchor(bottomHud, 0.0);
		AnchorPane.setRightAnchor(bottomHud, 0.0);
		AnchorPane.setBottomAnchor(bottomHud, 0.0);
		AnchorPane.setTopAnchor(leftUnitName, 15.0);
		AnchorPane.setLeftAnchor(leftUnitName, 0.0);
		AnchorPane.setTopAnchor(rightUnitName, 15.0);
		AnchorPane.setRightAnchor(rightUnitName, 0.0);
		retval_1.getChildren().addAll(gamePane, bottomHud, leftUnitName, rightUnitName);
		
		////////// The animation construction
		
		final Animation shakeAnimation = shakeAnimation(4, screenShakeTranslate);
		
		final ArrayList<Animation> animationParts = new ArrayList<>(strikes.size());
		animationParts.add(new PauseTransition(pauseDuration));
		
		// show both initiation animations at the same time
		animationParts.add(
			new ParallelTransition(
				  left.unit.getInitiateAnimation()
				, right.unit.getInitiateAnimation()
			)
		);
		
		animationParts.add(new PauseTransition(pauseDuration));
		
		// show each attack in sequence
		int leftCurrentHitpoints = left.initialCurrentHitpoints;
		int rightCurrentHitpoints = right.initialCurrentHitpoints;
		Point2D currentLeftOffset = mirrorX(initialUnitOffset);
		Point2D currentRightOffset = initialUnitOffset;
		double currentPan = 0;
		final double logicalScreenWidth = gamePanelSize.getWidth() / MagnificationBinding.compute(gamePanelSize.getWidth(), gamePanelSize.getHeight());
		
		for (int i = 0; i < strikes.size(); i++) {
			final Strike strike = strikes.get(i);
			final ConsecutiveAttackDescriptor consecutiveAttackDesc = consecutiveAttackDescriptor(strikes, i);
			final double centerPan = (currentLeftOffset.getX() + currentRightOffset.getX()) / 2;
			final boolean useCenterPan = Math.abs(currentLeftOffset.getX() - currentRightOffset.getX()) <= (logicalScreenWidth - distanceExtendPastPoint);
			final double leftPan = (useCenterPan ? centerPan : -currentLeftOffset.getX() + distanceExtendPastPoint - logicalScreenWidth / 2);
			final double rightPan = (useCenterPan ? centerPan : -currentRightOffset.getX() - distanceExtendPastPoint + logicalScreenWidth / 2);
			
			switch (strike.attacker) {
				case LEFT: {
					final int leftNewHp = leftCurrentHitpoints + strike.drain;
					final int rightNewHp = rightCurrentHitpoints - strike.damage;
					final Animation leftHealthbarAnimation = healthbarAnimation(healthbarLeft, leftCurrentHitpoints, leftNewHp);
					final Animation rightHealthbarAnimation = healthbarAnimation(healthbarRight, rightCurrentHitpoints, rightNewHp);
					
					final Point2D target = currentRightOffset.add(right.unit.getSpellTarget());
					
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
							(origin) -> left.spell.getAnimation(
								mirrorX(origin).add(currentLeftOffset),
								target,
								new SimpleDoubleTransition(
									Duration.millis(Math.abs(rightPan - leftPan)),
									panTranslate.xProperty(),
									leftPan,
									rightPan
								),
								new ParallelTransition(
									  shakeAnimation
									, leftHealthbarAnimation
									, rightHealthbarAnimation
								)
							)
							, target.subtract(currentLeftOffset)
							, consecutiveAttackDesc
							, strike.triggeredSkills
							, rightNewHp <= 0
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
					final Animation leftHealthbarAnimation = healthbarAnimation(healthbarLeft, leftCurrentHitpoints, leftNewHp);
					final Animation rightHealthbarAnimation = healthbarAnimation(healthbarRight, rightCurrentHitpoints, rightNewHp);
					
					Point2D target = currentLeftOffset.add(mirrorX(left.unit.getSpellTarget()));
					
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
							(origin) -> right.spell.getAnimation(
								origin.add(currentRightOffset),
								target,
								new SimpleDoubleTransition(
									Duration.millis(Math.abs(leftPan - rightPan)),
									panTranslate.xProperty(),
									rightPan,
									leftPan
								),
								new ParallelTransition(
									  shakeAnimation
									, leftHealthbarAnimation
									, rightHealthbarAnimation
								)
							)
							, target.subtract(currentRightOffset)
							, consecutiveAttackDesc
							, strike.triggeredSkills
							, leftNewHp <= 0
						)
					);
					
					leftCurrentHitpoints = leftNewHp;
					rightCurrentHitpoints = rightNewHp;
					currentPan = leftPan;
					break;
				}
			}
		}
		
		// If someone died, fade out the guys who died and make the ones who
		// didn't die perform a flourish.
		if (leftCurrentHitpoints <= 0 || rightCurrentHitpoints <= 0) {
			animationParts.add(new PauseTransition(pauseDuration.divide(2)));
			final ArrayList<Animation> deathParts = new ArrayList<>(2);
			if (leftCurrentHitpoints <= 0) {
				deathParts.add(deathFadeOutAnimation(left.unit.getNode()));
			}
			if (rightCurrentHitpoints <= 0) {
				deathParts.add(deathFadeOutAnimation(right.unit.getNode()));
			}
			if (rightCurrentHitpoints > 0) {
				deathParts.add(right.unit.getVictoryAnimation());
			}
			if (leftCurrentHitpoints > 0) {
				deathParts.add(left.unit.getVictoryAnimation());
			}
			
			final ParallelTransition deathTransition = new ParallelTransition();
			deathTransition.getChildren().addAll(deathParts);
			animationParts.add(deathTransition);
		}
		
		// pause a bit before fading back to the overworld
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
	
	private static ConsecutiveAttackDescriptor consecutiveAttackDescriptor(List<Strike> strikes, final int idx) {
		Side sideToMatch = strikes.get(idx).attacker;
		
		int left = 0;
		int j = idx;
		while (j >= 0 && strikes.get(j).attacker == sideToMatch) {
			j--;
			left++;
		}
		
		int right = -1;
		j = idx;
		while (j < strikes.size() && strikes.get(j).attacker == sideToMatch) {
			j++;
			right++;
		}
		
		return new ConsecutiveAttackDescriptor(left, left + right);
	}
	
	private static Animation deathFadeOutAnimation(Node n) {
		final ColorAdjust toWhiteEffect = new ColorAdjust();
		toWhiteEffect.setInput(n.getEffect());
		n.setEffect(toWhiteEffect);
		
		return new SequentialTransition(
			  new SimpleDoubleTransition(Duration.millis(300), toWhiteEffect.brightnessProperty(), 0, 1)
			, new SimpleDoubleTransition(Duration.millis(200), n.opacityProperty(), 1, 0)
		);
	}
	
	static final javafx.scene.layout.Border solidWhiteBorder =
		new javafx.scene.layout.Border(
			new javafx.scene.layout.BorderStroke(
				  Color.WHITE
				, javafx.scene.layout.BorderStrokeStyle.SOLID
				, javafx.scene.layout.CornerRadii.EMPTY
				, javafx.scene.layout.BorderStroke.MEDIUM
			)
		);
	
	static final javafx.scene.layout.Background solidBackground(Color c) {
		return new javafx.scene.layout.Background(
			new javafx.scene.layout.BackgroundFill(c, null, null)
		);
	}
	
	private static Pos withVCenter(HPos hpos) {
		switch (hpos) {
			case LEFT: return Pos.CENTER_LEFT;
			case CENTER: return Pos.CENTER;
			case RIGHT: return Pos.CENTER_RIGHT;
		}
		return Pos.CENTER;
	}
	
	private static HPos negate(HPos hpos) {
		switch (hpos) {
			case LEFT: return HPos.RIGHT;
			case CENTER: return HPos.CENTER;
			case RIGHT: return HPos.LEFT;
		}
		return HPos.CENTER;
	}
	
	private static ContentDisplay toContentDisplay(HPos hpos) {
		switch (hpos) {
			case LEFT: return ContentDisplay.LEFT;
			case CENTER: return ContentDisplay.CENTER;
			case RIGHT: return ContentDisplay.RIGHT;
		}
		return ContentDisplay.CENTER;
	}
	
	private static Label unitNameLabel(String text, Color bgColor, HPos alignment) {
		final Label retval = new Label(text);
		retval.setBorder(new javafx.scene.layout.Border(
			new javafx.scene.layout.BorderStroke(
				  Color.WHITE
				, javafx.scene.layout.BorderStrokeStyle.SOLID
				, javafx.scene.layout.CornerRadii.EMPTY
				, new javafx.scene.layout.BorderWidths(
					3,
					(alignment == HPos.RIGHT ? 0 : 3),
					3,
					(alignment == HPos.LEFT ? 0 : 3)
				  )
			)
		));
		retval.setBackground(solidBackground(bgColor));
		retval.setPrefWidth(120);
		retval.setTextFill(Color.WHITE);
		retval.setPadding(new javafx.geometry.Insets(3, 7, 3, 7));
		retval.setFont(Font.font("Sans", BOLD, 15));
		retval.setAlignment(withVCenter(alignment));
		return retval;
	}
	
	private static Label weaponLabel(String text, Node icon, Color bgColor, HPos alignment) {
		final Label retval = new Label(text, icon);
		retval.setBorder(new javafx.scene.layout.Border(
			new javafx.scene.layout.BorderStroke(
				  Color.WHITE
				, javafx.scene.layout.BorderStrokeStyle.SOLID
				, javafx.scene.layout.CornerRadii.EMPTY
				, new javafx.scene.layout.BorderWidths(
					3,
					(alignment == HPos.RIGHT ? 0 : 3),
					0,
					(alignment == HPos.LEFT ? 0 : 3)
				  )
			)
		));
		retval.setBackground(solidBackground(Color.GOLDENROD.darker()));
		retval.setPrefWidth(180);
		retval.setTextFill(Color.WHITE);
		retval.setPadding(new javafx.geometry.Insets(3, 3, 3, 3));
		retval.setFont(Font.font("Sans", BOLD, 15));
		retval.setAlignment(withVCenter(negate(alignment)));
		retval.setContentDisplay(toContentDisplay(negate(alignment)));
		return retval;
	}
	
	private static Point2D mirrorX(Point2D in) {
		return new Point2D(-1 * in.getX(), in.getY());
	}
}
