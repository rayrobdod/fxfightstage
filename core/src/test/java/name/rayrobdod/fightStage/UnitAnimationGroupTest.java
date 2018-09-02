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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public interface UnitAnimationGroupTest {
	
	UnitAnimationGroup getInstance();
	
	@Test
	default void getNode_isStable() {
		UnitAnimationGroup v = this.getInstance();
		Node n1 = v.objectBehindLayer();
		Node n2 = v.objectBehindLayer();
		Assertions.assertSame(n1, n2);
	}
	
	@Test
	default void getNode_notStatic() {
		UnitAnimationGroup v1 = this.getInstance();
		Node n1 = v1.objectBehindLayer();
		UnitAnimationGroup v2 = this.getInstance();
		Node n2 = v2.objectBehindLayer();
		Assertions.assertNotSame(n1, n2);
	}
	
	@Test
	default void getAttackAnimation_containsSpellAnimation() {
		UnitAnimationGroup v = this.getInstance();
		Animation spellAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAttackAnimation(
			  (x) -> spellAnim
			, v.getInitializingKeyValues(Side.LEFT, Point2D.ZERO)
			, Point2D.ZERO
			, new ConsecutiveAttackDescriptor(1, 1)
			, Collections.emptySet()
			, false
		);
		
		Assertions.assertEquals(1, UnitAnimationGroupTest.subAnimCount(dut, spellAnim));
	}
	
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues(double value) {
		UnitAnimationGroup v = this.getInstance();
		Map<WritableDoubleValue, Double> map = v.getInitializingKeyValues(Side.LEFT, new Point2D(value, 0.0));
		Assertions.assertEquals(value, v.getCurrentXOffset(map));
	}
	
	@Test
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues_0() {
		this.getCurrentXOffset_matchesValuePassedToInitializingKeyValues(0);
	}
	
	@Test
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues_50() {
		this.getCurrentXOffset_matchesValuePassedToInitializingKeyValues(50);
	}
	
	@Test
	default void getCurrentXOffset_matchesValuePassedToInitializingKeyValues_Neg50() {
		this.getCurrentXOffset_matchesValuePassedToInitializingKeyValues(-50);
	}
	
	
	public static int subAnimCount(Animation haystack, Animation needle) {
		if (haystack == needle) {
			return 1;
		} else if (haystack instanceof SequentialTransition) {
			List<Animation> childs = ((SequentialTransition) haystack).getChildren();
			return childs.stream().mapToInt(x -> UnitAnimationGroupTest.subAnimCount(x, needle)).sum();
		} else if (haystack instanceof ParallelTransition) {
			List<Animation> childs = ((ParallelTransition) haystack).getChildren();
			return childs.stream().mapToInt(x -> UnitAnimationGroupTest.subAnimCount(x, needle)).sum();
		} else {
			return 0;
		}
	}

}
