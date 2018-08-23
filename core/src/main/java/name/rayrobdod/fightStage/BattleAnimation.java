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
import javafx.beans.value.WritableDoubleValue;
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
		
		final Translate backgroundCenter = new Translate();
		final Translate backgroundPan = new Translate();
		final Scale backgroundScale = new Scale();
		
		final Node gameNodeBackgrounds = new Group(
			  left.spell.backgroundLayer()
			, right.spell.backgroundLayer()
		);
		gameNodeBackgrounds.getTransforms().add(backgroundScale);
		gameNodeBackgrounds.getTransforms().add(backgroundCenter);
		gameNodeBackgrounds.getTransforms().add(backgroundPan);
		
		final Node gameNode = new Group(
			  backgroundNode.apply(containerSize)
			, gameNodeBackgrounds
			, left.spell.objectBehindLayer()
			, right.spell.objectBehindLayer()
			, left.unit.objectBehindLayer()
			, right.unit.objectBehindLayer()
			, left.spell.objectFrontLayer()
			, right.spell.objectFrontLayer()
			// , zeroIndicator
			// , footPointIndicator
		);
		gameNode.getTransforms().add(magnifyTransform);
		gameNode.getTransforms().add(screenShakeTranslate);
		gameNode.getTransforms().add(centerTranslate);
		gameNode.getTransforms().add(panTranslate);
		
		final Pane gamePane = new Pane(gameNode);
		final SwipeAnimClip gamePaneClip = new SwipeAnimClip(gamePane.widthProperty(), gamePane.heightProperty());
		final DoubleBinding magnifyBinding = new MagnificationBinding(gamePane.widthProperty(), gamePane.heightProperty());
		gamePane.setClip(gamePaneClip.getNode());
		centerTranslate.xProperty().bind(gamePane.widthProperty().divide(2));
		centerTranslate.yProperty().bind(gamePane.heightProperty().multiply(2d/3d));
		magnifyTransform.xProperty().bind(magnifyBinding);
		magnifyTransform.yProperty().bind(magnifyBinding);
		magnifyTransform.pivotXProperty().bind(centerTranslate.xProperty());
		magnifyTransform.pivotYProperty().bind(centerTranslate.yProperty());
		
		backgroundScale.xProperty().bind(gamePane.widthProperty());
		backgroundScale.yProperty().bind(gamePane.heightProperty());
		backgroundCenter.xProperty().bind(centerTranslate.xProperty().negate().divide(gamePane.widthProperty()));
		backgroundCenter.yProperty().bind(centerTranslate.yProperty().negate().divide(gamePane.heightProperty()));
		backgroundPan.xProperty().bind(panTranslate.xProperty().negate().divide(gamePane.widthProperty()));
		backgroundPan.yProperty().bind(panTranslate.yProperty().negate().divide(gamePane.heightProperty()));
		
		final HealthBar healthbarLeft = new HealthBar(HPos.LEFT, left.teamColor, left.initialCurrentHitpoints, left.maximumHitpoints);
		final HealthBar healthbarRight = new HealthBar(HPos.RIGHT, right.teamColor, right.initialCurrentHitpoints, right.maximumHitpoints);
		final HudFlag leftUnitName = new HudFlag(HPos.LEFT, Color.WHITE, left.teamColor);
		final HudFlag rightUnitName = new HudFlag(HPos.RIGHT, Color.WHITE, right.teamColor);
		final Label leftWeaponName = weaponLabel(left.weaponName, left.weaponIcon, left.teamColor, HPos.LEFT, magnifyBinding);
		final Label rightWeaponName = weaponLabel(right.weaponName, right.weaponIcon, right.teamColor, HPos.RIGHT, magnifyBinding);
		final List<HudFlag> leftModifiers = Stream.generate(() -> new HudFlag(HPos.LEFT, Color.BLACK, Color.GOLD)).limit(maxModifiersSize).collect(Collectors.toList());
		final List<HudFlag> rightModifiers = Stream.generate(() -> new HudFlag(HPos.RIGHT, Color.BLACK, Color.GOLD)).limit(maxModifiersSize).collect(Collectors.toList());
		
		
		final GridPane bottomHud = new GridPane();
		bottomHud.addRow(0, leftWeaponName, rightWeaponName);
		bottomHud.addRow(1, healthbarLeft.getNode(), healthbarRight.getNode());
		GridPane.setHgrow(healthbarLeft.getNode(), Priority.ALWAYS);
		GridPane.setHgrow(healthbarRight.getNode(), Priority.ALWAYS);
		GridPane.setHalignment(leftWeaponName, HPos.LEFT);
		GridPane.setHalignment(rightWeaponName, HPos.RIGHT);
		healthbarLeft.scaleProperty().bind(magnifyBinding);
		healthbarRight.scaleProperty().bind(magnifyBinding);
		leftUnitName.scaleProperty().bind(magnifyBinding);
		rightUnitName.scaleProperty().bind(magnifyBinding);
		
		final VBox leftModifierBox = new VBox(15, leftUnitName.getNode());
		leftModifierBox.setAlignment(Pos.TOP_LEFT);
		for (HudFlag x : leftModifiers) {
			leftModifierBox.getChildren().add(x.getNode());
			x.scaleProperty().bind(magnifyBinding);
		}
		final VBox rightModifierBox = new VBox(15, rightUnitName.getNode());
		rightModifierBox.setAlignment(Pos.TOP_RIGHT);
		for (HudFlag x : rightModifiers) {
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
		
		// transition in
		{
			final Animation baseAnim = gamePaneClip.swipeInAnimation();
			animationParts.add(new ParallelTransition(
				leftUnitName.fadeInAnimation(left.unitName, baseAnim.getCycleDuration().multiply(2d/3d)),
				rightUnitName.fadeInAnimation(right.unitName, baseAnim.getCycleDuration().multiply(2d/3d)),
				baseAnim
			));
		}
		
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
		BattlePanAnimations currentPan = new BattlePanAnimations(
			panTranslate.xProperty(),
			panTranslate.yProperty(),
			0,
			0
		);
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
			final double attackerPan = (strike.attacker == Side.LEFT ? leftPan : rightPan);
			final double defenderPan = (strike.attacker == Side.LEFT ? rightPan : leftPan);
			currentPan = currentPan.withNewFocusCoords(attackerPan, 0, defenderPan, 0);
			final BattlePanAnimations currentPanFinal = currentPan;
			
			final Animation attackModifierInAnims = HudFlag.seqFadeInAnim(
				(strike.attacker == Side.LEFT ? leftModifiers : rightModifiers),
				strike.attackerModifiers
			);
			final Animation defenderModifierInAnims = HudFlag.seqFadeInAnim(
				(strike.attacker == Side.LEFT ? rightModifiers : leftModifiers),
				strike.defenderModifiers
			);
			final Animation attackModifierOutAnims = HudFlag.seqFadeOutAnim(
				(strike.attacker == Side.LEFT ? leftModifiers : rightModifiers),
				strike.attackerModifiers
			);
			final Animation defenderModifierOutAnims = HudFlag.seqFadeOutAnim(
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
				currentPan.panToAttacker()
			);
			animationParts.add(new ParallelTransition(
				attackModifierInAnims,
				attacker.unit.getAttackAnimation(
					(origin) -> attacker.spell.getAnimation(
						origin,
						target,
						currentPanFinal,
						new ShakeAnimationFactory(screenShakeTranslate),
						new ParallelTransition(
							  hitAnimation
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
		}
		
		// If someone died, fade out the guys who died and make the ones who
		// didn't die perform a flourish.
		if (leftCurrentHitpoints <= 0 || rightCurrentHitpoints <= 0) {
			animationParts.add(new PauseTransition(pauseDuration.divide(2)));
			final ArrayList<Animation> deathParts = new ArrayList<>(2);
			if (leftCurrentHitpoints <= 0) {
				deathParts.add(deathFadeOutAnimation(left.unit.objectBehindLayer()));
			}
			if (rightCurrentHitpoints <= 0) {
				deathParts.add(deathFadeOutAnimation(right.unit.objectBehindLayer()));
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
		
		// fade out
		animationParts.add(new ParallelTransition(
			leftUnitName.fadeOutAnimation(),
			rightUnitName.fadeOutAnimation(),
			gamePaneClip.swipeOutAnimation()
		));
		
		final SequentialTransition retval_2 = new SequentialTransition();
		retval_2.getChildren().addAll(animationParts);
	
		
		return new NodeAnimationPair(retval_1, retval_2);
	}
	
	private static Animation healthbarAnimation(HealthBar hb, int from, int to) {
		final Duration timePerTick = Duration.millis(50);
		final Duration time = timePerTick.multiply(Math.abs(to - from));
		
		return Animations.simpleAnimation(time, hb.currentHealthProperty(), from, to);
	}
	
	private static final class ShakeAnimationFactory implements ShakeAnimationBiFunction {
		private static final Duration shakeFrequency = Duration.millis(160);
		private static final double shakeFullIntensityFraction = 0.5;
		
		private final WritableDoubleValue xProperty;
		private final WritableDoubleValue yProperty;
		
		private static final double DEFAULT_INTENSITY = 6;
		private static final Duration DEFAULT_DURATION = shakeFrequency;
		
		public ShakeAnimationFactory(
			  javafx.scene.transform.Translate translate
		) {
			this.xProperty = translate.xProperty();
			this.yProperty = translate.yProperty();
		}
		
		public Animation apply() {
			return this.apply(DEFAULT_INTENSITY, DEFAULT_DURATION);
		}
		
		public Animation apply(double intensity) {
			return this.apply(intensity, DEFAULT_DURATION);
		}
		
		public Animation apply(Duration duration) {
			return this.apply(DEFAULT_INTENSITY, shakeFrequency);
		}
		
		public Animation apply(double intensity, Duration duration) {
			final Timeline retval = new Timeline();
			retval.getKeyFrames().add(new KeyFrame(Duration.ZERO,
				new KeyValue(xProperty, 0, Interpolator.LINEAR),
				new KeyValue(yProperty, 0, Interpolator.LINEAR)
			));
			
			for (Duration i = shakeFrequency.divide(4); i.lessThan(duration); i = i.add(shakeFrequency)) {
				final double leftFraction = i.toMillis() / duration.toMillis();
				final double rightFraction = i.add(shakeFrequency.divide(2)).toMillis() / duration.toMillis();
				final double leftIntensity = intensity * Math.min(1.0, (1.0 - leftFraction) / shakeFullIntensityFraction);
				final double rightIntensity = intensity * Math.min(1.0, (1.0 - rightFraction) / shakeFullIntensityFraction);
				
				retval.getKeyFrames().add(new KeyFrame(i,
					new KeyValue(xProperty, leftIntensity, Interpolator.LINEAR),
					new KeyValue(yProperty, -leftIntensity, Interpolator.LINEAR)
				));
				retval.getKeyFrames().add(new KeyFrame(i.add(shakeFrequency.divide(2)),
					new KeyValue(xProperty, -rightIntensity, Interpolator.LINEAR),
					new KeyValue(yProperty, rightIntensity, Interpolator.LINEAR)
				));
			}
			retval.getKeyFrames().add(new KeyFrame(duration,
				new KeyValue(xProperty, 0, Interpolator.LINEAR),
				new KeyValue(yProperty, 0, Interpolator.LINEAR)
			));
			
			return retval;
		}
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
			  Animations.simpleAnimation(Duration.millis(300), toWhiteEffect.brightnessProperty(), 0, 1)
			, Animations.simpleAnimation(Duration.millis(200), n.opacityProperty(), 1, 0)
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
