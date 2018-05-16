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
package name.rayrobdod.fightStage.spellAnimationGroup.electricty;

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Group;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;

/**
 * Generates an animation that displays the results of a JaggedLineFactory
 * in a Group
 */
public interface ElectricAnimationFactory {
	/**
	 * A function (usually an {@link ElectricAnimationFactory} implementation's
	 * constructor) that creates a {@link ElectricAnimationFactory} from a
	 * JaggedLineFactory to show and the Group to show it in
	 */
	@FunctionalInterface
	public static interface Factory {
		public ElectricAnimationFactory build(
			  final JaggedLineFactory lineGenerator
			, final Group foreground
		);
	}
	
	/**
	 * @see name.rayrobdod.fightStage.SpellAnimationGroup#getAnimation
	 */
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	);
}
