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
package name.rayrobdod.fightStage.previewer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ButtonMatchers;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroup;

// this uses instance variables to communicate between @Start and @Test, hence the need for PER_METHOD
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(ApplicationExtension.class)
@Tag("robot")
public final class MediaControlPanelTest {
	
	private final static Duration HALF_SECOND = Duration.millis(500);
	
	private AtomicInteger actionInvocationCount = new AtomicInteger();
	private ObjectProperty<Animation> animProp = new SimpleObjectProperty<>();
	
	@Start
	public void onStart(Stage stage) {
		MediaControlPanel dut = new MediaControlPanel(
			animProp,
			(e) -> actionInvocationCount.incrementAndGet()
		);
		// My normal method of `.getScene().setRoot(…)` results in some concurrent modification exceptions
		stage.setScene(new Scene(new StackPane(dut.getNode())));
		stage.show();
	}
	
	@Test
	public void checkInitialConditions() {
		Assertions.assertEquals(0, actionInvocationCount.get());
		Assertions.assertNull(animProp.get());
		FxAssert.verifyThat(".button-play", ButtonMatchers.isDefaultButton());
		FxAssert.verifyThat(".button-stop", NodeMatchers.isDisabled());
		FxAssert.verifyThat(".progress-bar", (x) -> ((ProgressBar) x).getProgress() == 0.0);
		
	}
	
	@Test
	public void givenAnimIsNull_whenButtonPlayIsClicked_thenPlayButtonEventIsCalled(FxRobot robot) {
		// given:
		this.checkInitialConditions();
		// when:
		robot.clickOn(".button-play");
		
		// then:
		Assertions.assertEquals(1, actionInvocationCount.get());
	}
	
	@Test
	public void givenAnimIsNotNull_whenButtonPlayIsClicked_thenPlayButtonEventIsNotCalled(FxRobot robot) {
		// given:
		this.checkInitialConditions();
		robot.interact(() -> animProp.set(new PauseTransition(HALF_SECOND)));
		// when:
		robot.clickOn(".button-play");
		// then:
		Assertions.assertEquals(0, actionInvocationCount.get());
	}
	
	@Test
	public void whenAnimIsNotNull_thenStopButtonIsEnabled(FxRobot robot) {
		// given:
		this.checkInitialConditions();
		// when:
		robot.interact(() -> animProp.set(new PauseTransition(HALF_SECOND)));
		// then:
		FxAssert.verifyThat(".button-stop", NodeMatchers.isEnabled());
	}
	
	@Test
	public void givenAnimIsNotNull_whenStopButtonIsClicked_thenAnimOnFinishedIsCalled(FxRobot robot) {
		AtomicInteger finishedInvocationCount = new AtomicInteger();
		Animation anim = new PauseTransition(HALF_SECOND);
		anim.setOnFinished(x -> finishedInvocationCount.incrementAndGet());
		
		// given:
		this.checkInitialConditions();
		robot.interact(() -> animProp.set(anim));
		// when:
		robot.clickOn(".button-stop");
		// then:
		Assertions.assertEquals(1, finishedInvocationCount.get());
	}
	
	private void progressbarShowsAnimationProgress(FxRobot robot, double progress) {
		// given:
		this.checkInitialConditions();
		// when:
		robot.interact(() -> {
			animProp.set(new PauseTransition(HALF_SECOND));
			animProp.get().jumpTo(HALF_SECOND.multiply(progress));
		});
		// then:
		FxAssert.verifyThat(".progress-bar", (x) -> ((ProgressBar) x).getProgress() == progress);
	}
	
	@Test
	public void progressbarShowsAnimationProgress_half(FxRobot robot) {
		this.progressbarShowsAnimationProgress(robot, 0.5);
	}
	
	@Test
	public void progressbarShowsAnimationProgress_third(FxRobot robot) {
		this.progressbarShowsAnimationProgress(robot, 1d / 3d);
	}
	
	@Test
	public void progressbarShowsAnimationProgress_twoFifths(FxRobot robot) {
		this.progressbarShowsAnimationProgress(robot, 2d / 5d);
	}
	
	@Test
	public void previousAnimationDoesNotAffectProgressbar(FxRobot robot) {
		final StackPane gamePane = new StackPane();
		final PlayBattleAnimationEventHandler evh = new PlayBattleAnimationEventHandler(
			  gamePane
			, animProp
			, NilUnitAnimationGroup::new
			, NilUnitAnimationGroup::new
			, NilSpellAnimationGroup::new
			, NilSpellAnimationGroup::new
			, () -> 60
			, () -> 60
			, () -> 60
			, () -> 60
			, () -> 200
		);
		// given: an animation was stopped partway through
		this.checkInitialConditions();
		robot.interact(() -> evh.handle(null));
		Duration animDur = animProp.get().getTotalDuration();
		robot.sleep((long) animDur.divide(2).toMillis());
		robot.clickOn(".button-stop");
		// when: a new animation is started
		robot.interact(() -> evh.handle(null));
		robot.sleep((long) animDur.divide(2).toMillis());
		// then: the progress bar's progress does not reset partway through
		FxAssert.verifyThat(".progress-bar", (x) -> ((ProgressBar) x).getProgress() != 0);
	}
	
	
	private static class NilUnitAnimationGroup implements UnitAnimationGroup {
		private Node node = new Group();
		public Node getNode() {return node;}
		public Point2D getSpellTarget(Map<DoubleProperty, Double> _1) {return Point2D.ZERO;}
		public double getCurrentXOffset(Map<DoubleProperty, Double> _1) {return 0;}
		public Animation getAttackAnimation(
			  Function<Point2D, Animation> spellAnimationFun
			, Map<DoubleProperty, Double> rolloverKeyValues
			, Point2D target
			, ConsecutiveAttackDescriptor consecutiveAttackDesc
			, Set<AttackModifier> attackerModifiers
			, boolean isFinisher
		) { return Animations.nil(); }
		public Map<DoubleProperty, Double> getInitializingKeyValues(
			  Side side
			, Point2D initialOffset
		) {return new java.util.HashMap<>();}
	}
	
	private static class NilSpellAnimationGroup implements SpellAnimationGroup {
		private Node fore = new Group();
		private Node back = new Group();
		public Node getBackground() {return back;}
		public Node getForeground() {return fore;}
		public Animation getAnimation(
			  Point2D origin
			, Point2D target
			, Animation panAnimation
			, Animation hpAndShakeAnimation
		) { return Animations.nil(); }
	}
}
