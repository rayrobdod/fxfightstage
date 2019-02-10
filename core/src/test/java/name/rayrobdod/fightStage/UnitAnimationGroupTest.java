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

import org.junit.jupiter.api.Test;

public interface UnitAnimationGroupTest {
	
	UnitAnimationGroup getInstance();
	
	@Test
	default void getObjectBehindLayer_isStable() {
		UnitAnimationGroupTests.getObjectBehindLayer_isStable(this::getInstance);
	}
	
	@Test
	default void getObjectBehindLayer_notStatic() {
		UnitAnimationGroupTests.getObjectBehindLayer_notStatic(this::getInstance);
	}
	
	@Test
	default void getObjectFrontLayer_isStable() {
		UnitAnimationGroupTests.getObjectFrontLayer_isStable(this::getInstance);
	}
	
	@Test
	default void getObjectFrontLayer_notStatic() {
		UnitAnimationGroupTests.getObjectFrontLayer_notStatic(this::getInstance);
	}
	
	@Test
	default void getAttackAnimation_containsSpellAnimation() {
		UnitAnimationGroupTests.getAttackAnimation_containsSpellAnimation(this::getInstance);
	}
	
	@Test
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues_0() {
		UnitAnimationGroupTests.getCurrentXOffset_matchesValuePassedToInitializingKeyValues(this::getInstance, 0);
	}
	
	@Test
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues_50() {
		UnitAnimationGroupTests.getCurrentXOffset_matchesValuePassedToInitializingKeyValues(this::getInstance, 50);
	}
	
	@Test
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues_Neg50() {
		UnitAnimationGroupTests.getCurrentXOffset_matchesValuePassedToInitializingKeyValues(this::getInstance, -50);
	}
}
