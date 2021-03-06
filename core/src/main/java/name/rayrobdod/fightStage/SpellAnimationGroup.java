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

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * An object that describes a spell animation.
 * 
 * The coordinate system should match BattleAnimation's coordinate system,
 * because everything should be done relative to the `origin` and `target` coordinates,
 * and those will be given in the BattleAnimation's coordinate system.
 */
public interface SpellAnimationGroup {
	
	/**
	 * A Node that contains every visual element that will appear behind
	 * the characters from the perspective of the player.
	 * 
	 * For any given instance of SpellAnimationGroup, this method must return
	 * the same object upon each invocation. Different instances of
	 * SpellAnimationGroup, this function must return different objects.
	 * 
	 * @return the nodes that appear behind the characters
	 */
	public Node objectBehindLayer();
	
	/**
	 * A Node that contains every visual element that will appear between
	 * the characters and the player
	 * 
	 * For any given instance of SpellAnimationGroup, this method must return
	 * the same object upon each invocation. Different instances of
	 * SpellAnimationGroup, this function must return different objects.
	 * 
	 * @return the nodes that appear between the characters and the player
	 */
	public Node objectFrontLayer();
	
	/**
	 * A screen-filling background layer
	 * 
	 * This layer has a coordinate system such that 0,0 is one corner of the screen
	 * and 1,1 is the opposite corner of the screen.
	 * 
	 * For any given instance of SpellAnimationGroup, this method must return
	 * the same object upon each invocation. Different instances of
	 * SpellAnimationGroup, this function must return different objects.
	 * 
	 * @return the nodes that appear between the characters and the player
	 */
	public Node backgroundLayer();
	
	/**
	 * Returns an animation to be used for an attack animation
	 * 
	 * The returned Animation must include `panAnimation` exactly once,
	 * and must include `hitAnimation` exactly once. It is recommended
	 * that one `shakeAnimationFactory` application is included in parallel
	 * with the hitAnimation. If more than one `shakeAnimationFactory`
	 * application is used, they must not overlap time-wise.
	 * 
	 * @param origin the origin point of the spell animation
	 * @param target the target point of the spell animation
	 * @param panAnimations a factory of animations that move the viewport
	 * @param shakeAnimationFactory A factory of animations that will cause the camera to shake
	 * @param hitAnimation the effects that happen to indicate a hit.
	 * @return an animation representing this spell
	 */
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimations,
		ShakeAnimationBiFunction shakeAnimationFactory,
		Animation hitAnimation
	);
}
