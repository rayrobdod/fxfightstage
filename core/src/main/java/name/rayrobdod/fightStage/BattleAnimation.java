/*
 * Copyright 2018 Raymond Dodge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.rayrobdod.fightStage;

import static javafx.scene.text.FontWeight.BOLD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 * The entry point to this library.
 */
public final class BattleAnimation {
	private BattleAnimation() {}
	
	private static final Duration pauseDuration = Duration.millis(1000);
	public static final double GROUND_Y = 0;
	private static final double distanceExtendPastPoint = 150;
	private static final double distanceWithNoPan = 40;
	private static final double sideNoteWidth = 120;
	
	
	public static NodeAnimationPair buildAnimation(
		Function<Dimension2D, Node> backgroundNode,
		Dimension2D containerSize,
		double verticalDistance,
		AggregateSideParams left,
		AggregateSideParams right,
		List<Strike> strikes
	) {
		///////////// The node construction
		final int maxModifiersSize = strikes.stream().mapToInt(Strike::maxModifierSize).max().orElse(0);
		
		final Rectangle footPointIndicator = new Rectangle(-verticalDistance / 2, -500, verticalDistance, 1000);
		footPointIndicator.setFill(Color.rgb(255, 0, 255, 0.5));
		final Circle zeroIndicator = new Circle(0, 0, 5);
		
		final Translate screenShakeTranslate = new Translate();
		final Translate centerTranslate = new Translate();
		final Translate panTranslate = new Translate();
		final Scale magnifyTransform = new Scale();
		
		final Node gameNode = new Group(
			  backgroundNode.apply(containerSize)
			, left.spell.getBackground()
			, right.spell.getBackground()
			, left.unit.getNode()
			, right.unit.getNode()
			, left.spell.getForeground()
			, right.spell.getForeground()
			// , zeroIndicator
			// , footPointIndicator
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
		
		
		final HealthBar healthbarLeft = new HealthBar(HPos.LEFT, left.teamColor, left.initialCurrentHitpoints, left.maximumHitpoints);
		final HealthBar healthbarRight = new HealthBar(HPos.RIGHT, right.teamColor, right.initialCurrentHitpoints, right.maximumHitpoints);
		final Label leftUnitName = unitNameLabel(left.unitName, left.teamColor, HPos.LEFT, magnifyBinding);
		final Label rightUnitName = unitNameLabel(right.unitName, right.teamColor, HPos.RIGHT, magnifyBinding);
		final Label leftWeaponName = weaponLabel(left.weaponName, left.weaponIcon, left.teamColor, HPos.LEFT, magnifyBinding);
		final Label rightWeaponName = weaponLabel(right.weaponName, right.weaponIcon, right.teamColor, HPos.RIGHT, magnifyBinding);
		final List<ModifierLabel> leftModifiers = Stream.generate(() -> new ModifierLabel(HPos.LEFT)).limit(maxModifiersSize).collect(Collectors.toList());
		final List<ModifierLabel> rightModifiers = Stream.generate(() -> new ModifierLabel(HPos.RIGHT)).limit(maxModifiersSize).collect(Collectors.toList());
		
		
		final GridPane bottomHud = new GridPane();
		bottomHud.addRow(0, leftWeaponName, rightWeaponName);
		bottomHud.addRow(1, healthbarLeft.getNode(), healthbarRight.getNode());
		GridPane.setHgrow(healthbarLeft.getNode(), Priority.ALWAYS);
		GridPane.setHgrow(healthbarRight.getNode(), Priority.ALWAYS);
		GridPane.setHalignment(leftWeaponName, HPos.LEFT);
		GridPane.setHalignment(rightWeaponName, HPos.RIGHT);
		healthbarLeft.scaleProperty().bind(magnifyBinding);
		healthbarRight.scaleProperty().bind(magnifyBinding);
		
		final VBox leftModifierBox = new VBox(15, leftUnitName);
		leftModifierBox.setAlignment(Pos.TOP_LEFT);
		for (ModifierLabel x : leftModifiers) {
			leftModifierBox.getChildren().add(x.getNode());
			x.scaleProperty().bind(magnifyBinding);
		}
		final VBox rightModifierBox = new VBox(15, rightUnitName);
		rightModifierBox.setAlignment(Pos.TOP_RIGHT);
		for (ModifierLabel x : rightModifiers) {
			rightModifierBox.getChildren().add(x.getNode());
			x.scaleProperty().bind(magnifyBinding);
		}
		
		
		final AnchorPane retval_1 = new AnchorPane();
		AnchorPane.setTopAnchor(gamePane, 0.0);
		AnchorPane.setLeftAnchor(gamePane, 0.0);
		AnchorPane.setRightAnchor(gamePane, 0.0);
		AnchorPane.setBottomAnchor(gamePane, 0.0);
		AnchorPane.setLeftAnchor(bottomHud, 0.0);
		AnchorPane.setRightAnchor(bottomHud, 0.0);
		AnchorPane.setBottomAnchor(bottomHud, 0.0);
		AnchorPane.setTopAnchor(leftModifierBox, 15.0);
		AnchorPane.setLeftAnchor(leftModifierBox, 0.0);
		AnchorPane.setTopAnchor(rightModifierBox, 15.0);
		AnchorPane.setRightAnchor(rightModifierBox, 0.0);
		retval_1.getChildren().addAll(gamePane, bottomHud, leftModifierBox, rightModifierBox);
		
		////////// The animation construction
		
		final ArrayList<Animation> animationParts = new ArrayList<>(strikes.size());
		final Animation shakeAnimation = shakeAnimation(4, screenShakeTranslate);
		
		// place the units at their starting location
		final Point2D initialUnitOffset = new Point2D(
			verticalDistance / 2,
			GROUND_Y
		);
		final Map<DoubleProperty, Double> leftRolloverValues = left.unit.getInitializingKeyValues(Side.LEFT, mirrorX(initialUnitOffset));
		final Map<DoubleProperty, Double> rightRolloverValues = right.unit.getInitializingKeyValues(Side.RIGHT, initialUnitOffset);
		{
			final Map<DoubleProperty, Double> initialValues = new java.util.HashMap<>();
			initialValues.putAll(leftRolloverValues);
			initialValues.putAll(rightRolloverValues);
			final Timeline initializeAnim = new Timeline();
			initializeAnim.getKeyFrames().add(propValueMapToDiscreteKeyFrame(initialValues, Duration.ZERO));
			initializeAnim.getKeyFrames().add(propValueMapToDiscreteKeyFrame(initialValues, Duration.ONE));
			animationParts.add(initializeAnim);
		}
		
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
		double currentPan = 0;
		final double logicalScreenWidth = containerSize.getWidth() / MagnificationBinding.compute(containerSize.getWidth(), containerSize.getHeight());
		
		for (int i = 0; i < strikes.size(); i++) {
			final double currentLeftOffset = left.unit.getCurrentXOffset(leftRolloverValues);
			final double currentRightOffset = right.unit.getCurrentXOffset(rightRolloverValues);
			final Strike strike = strikes.get(i);
			final ConsecutiveAttackDescriptor consecutiveAttackDesc = consecutiveAttackDescriptor(strikes, i);
			final double centerPan = -(currentLeftOffset + currentRightOffset) / 2;
			final boolean useCenterPan = Math.abs(currentLeftOffset - currentRightOffset) <= (logicalScreenWidth - distanceExtendPastPoint * 2);
			final double leftPan = (useCenterPan ? centerPan : -currentLeftOffset + distanceExtendPastPoint - logicalScreenWidth / 2);
			final double rightPan = (useCenterPan ? centerPan : -currentRightOffset - distanceExtendPastPoint + logicalScreenWidth / 2);
			
			
			final int leftHpDelta = (strike.attacker == Side.LEFT ? strike.drain : -strike.damage);
			final int rightHpDelta = (strike.attacker == Side.RIGHT ? strike.drain : -strike.damage);
			final int leftNewHp = leftCurrentHitpoints + leftHpDelta;
			final int rightNewHp = rightCurrentHitpoints + rightHpDelta;
			final Animation leftHealthbarAnimation = healthbarAnimation(healthbarLeft, leftCurrentHitpoints, leftNewHp);
			final Animation rightHealthbarAnimation = healthbarAnimation(healthbarRight, rightCurrentHitpoints, rightNewHp);
			final boolean isFinisher = (strike.attacker == Side.LEFT ? rightNewHp : leftNewHp) <= 0;
			
			AggregateSideParams attacker = (strike.attacker == Side.LEFT ? left : right);
			AggregateSideParams defender = (strike.attacker == Side.LEFT ? right : left);
			final Map<DoubleProperty, Double> attackerRolloverValues = (strike.attacker == Side.LEFT ? leftRolloverValues : rightRolloverValues);
			final Map<DoubleProperty, Double> defenderRolloverValues = (strike.attacker == Side.LEFT ? rightRolloverValues : leftRolloverValues);
			final double attackerPan2 = (strike.attacker == Side.LEFT ? leftPan : rightPan);
			final double attackerPan = (Math.abs(attackerPan2 - currentPan) < distanceWithNoPan ? currentPan : attackerPan2);
			final double defenderPan2 = (strike.attacker == Side.LEFT ? rightPan : leftPan);
			final double defenderPan = (Math.abs(defenderPan2 - attackerPan) < distanceWithNoPan ? attackerPan : defenderPan2);
			
			final Animation attackModifierInAnims = ModifierLabel.seqFadeInAnim(
				(strike.attacker == Side.LEFT ? leftModifiers : rightModifiers),
				strike.attackerModifiers
			);
			final Animation defenderModifierInAnims = ModifierLabel.seqFadeInAnim(
				(strike.attacker == Side.LEFT ? rightModifiers : leftModifiers),
				strike.defenderModifiers
			);
			final Animation attackModifierOutAnims = ModifierLabel.seqFadeOutAnim(
				(strike.attacker == Side.LEFT ? leftModifiers : rightModifiers),
				strike.attackerModifiers
			);
			final Animation defenderModifierOutAnims = ModifierLabel.seqFadeOutAnim(
				(strike.attacker == Side.LEFT ? rightModifiers : leftModifiers),
				strike.defenderModifiers
			);
			
			final Point2D target = defender.unit.getSpellTarget(defenderRolloverValues);
			final Animation hitAnimation = defender.unit.getHitAnimation(
				  defenderRolloverValues
				, strike.attackerModifiers
				, strike.defenderModifiers
				, isFinisher
			);
			animationParts.add(
				Animations.doubleSimpleAnimation(
					Duration.millis(Math.abs(currentPan - attackerPan)),
					panTranslate.xProperty(),
					currentPan,
					attackerPan
				)
			);
			animationParts.add(new ParallelTransition(
				attackModifierInAnims,
				attacker.unit.getAttackAnimation(
					(origin) -> attacker.spell.getAnimation(
						origin,
						target,
						Animations.doubleSimpleAnimation(
							Duration.millis(Math.abs(defenderPan - attackerPan)),
							panTranslate.xProperty(),
							attackerPan,
							defenderPan
						),
						new ParallelTransition(
							  shakeAnimation
							, hitAnimation
							, defenderModifierInAnims
							, leftHealthbarAnimation
							, rightHealthbarAnimation
						)
					  )
					, attackerRolloverValues
					, target
					, consecutiveAttackDesc
					, strike.attackerModifiers
					, isFinisher
				)
			));
			animationParts.add(new ParallelTransition(
				attackModifierOutAnims,
				defenderModifierOutAnims
			));
			
			leftCurrentHitpoints = leftNewHp;
			rightCurrentHitpoints = rightNewHp;
			currentPan = defenderPan;
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
		
		return Animations.integerSimpleAnimation(time, hb.currentHealthProperty(), from, to);
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
			  Animations.doubleSimpleAnimation(Duration.millis(300), toWhiteEffect.brightnessProperty(), 0, 1)
			, Animations.doubleSimpleAnimation(Duration.millis(200), n.opacityProperty(), 1, 0)
		);
	}
	
	static final javafx.scene.layout.Background solidBackground(Color c) {
		return new javafx.scene.layout.Background(
			new javafx.scene.layout.BackgroundFill(c, null, null)
		);
	}
	
	static Pos withVCenter(HPos hpos) {
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
	
	private static Label unitNameLabel(String text, Color bgColor, HPos alignment, DoubleBinding scale) {
		final Label retval = new Label(text);
		retval.borderProperty().bind(
			Bindings.solidScalableWidthBorder(
				Color.WHITE,
				3,
				(alignment == HPos.RIGHT ? 0 : 3),
				3,
				(alignment == HPos.LEFT ? 0 : 3),
				scale
			)
		);
		retval.setBackground(solidBackground(bgColor));
		retval.prefWidthProperty().bind(scale.multiply(sideNoteWidth));
		retval.setTextFill(Color.WHITE);
		retval.paddingProperty().bind(Bindings.insetScale(new Insets(3, 7, 3, 7), scale));
		retval.fontProperty().bind(Bindings.fontScale(Font.font("Sans", BOLD, 15), scale));
		retval.setAlignment(withVCenter(alignment));
		return retval;
	}
	
	private static Label weaponLabel(String text, Node icon, Color bgColor, HPos alignment, DoubleBinding scale) {
		final Group iconG = new Group(icon);
		iconG.scaleXProperty().bind(scale);
		iconG.scaleYProperty().bind(scale);
		final Group iconGG = new Group(iconG);
		
		final Label retval = new Label(text, iconGG);
		retval.borderProperty().bind(
			Bindings.solidScalableWidthBorder(
				Color.WHITE,
				3,
				(alignment == HPos.RIGHT ? 0 : 3),
				0,
				(alignment == HPos.LEFT ? 0 : 3),
				scale
			)
		);
		retval.setBackground(solidBackground(Color.GOLDENROD.darker()));
		retval.prefWidthProperty().bind(scale.multiply(180));
		retval.setTextFill(Color.WHITE);
		retval.paddingProperty().bind(Bindings.insetScale(new Insets(3, 3, 3, 3), scale));
		retval.fontProperty().bind(Bindings.fontScale(Font.font("Sans", BOLD, 15), scale));
		retval.setAlignment(withVCenter(negate(alignment)));
		retval.setContentDisplay(toContentDisplay(negate(alignment)));
		return retval;
	}
	
	private static Point2D mirrorX(Point2D in) {
		return new Point2D(-1 * in.getX(), in.getY());
	}
	
	public static KeyFrame propValueMapToDiscreteKeyFrame(
		Map<DoubleProperty, Double> map,
		Duration time
	) {
		KeyValue[] values = map.entrySet().stream()
			.map(x -> new KeyValue(x.getKey(), x.getValue(), Interpolator.DISCRETE))
			.toArray(KeyValue[]::new);
		return new KeyFrame(time, values);
	}
}
