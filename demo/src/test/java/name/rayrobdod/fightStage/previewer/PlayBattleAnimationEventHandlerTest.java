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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.framework.junit5.ApplicationExtension;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.AttackModifier;
import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ConsecutiveAttackDescriptor;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.Side;
import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroup;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(ApplicationExtension.class)
public final class PlayBattleAnimationEventHandlerTest {
	
	@Test
	public void onHandleSetsAndStartsAnimation() {
		final ObjectProperty<Animation> animProp = new SimpleObjectProperty<>();
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
			, Collections::emptyList
			, () -> 200
		);
		// when:
		evh.handle(null);
		// then:
		Assertions.assertEquals(Status.RUNNING, animProp.get().getStatus());
	}
	
	@Test
	public void onFinishedBothStopsAndUnsetsThePropertyValue() {
		final ObjectProperty<Animation> animProp = new SimpleObjectProperty<>();
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
			, Collections::emptyList
			, () -> 200
		);
		evh.handle(null);
		final Animation anim = animProp.get();
		// when:
		anim.getOnFinished().handle(null);
		// then:
		Assertions.assertNull(animProp.get());
		Assertions.assertEquals(Status.STOPPED, anim.getStatus());
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
			, BattlePanAnimations panAnimation
			, ShakeAnimationBiFunction shakeAnimation
			, Animation hitAnimation
		) { return Animations.nil(); }
	}
}
