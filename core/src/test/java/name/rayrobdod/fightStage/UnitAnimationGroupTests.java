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

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;

public final class UnitAnimationGroupTests {
	private UnitAnimationGroupTests() {}
	
	public static void getNode_isStable(Supplier<UnitAnimationGroup> s) {
		UnitAnimationGroup v = s.get();
		Node n1 = v.objectBehindLayer();
		Node n2 = v.objectBehindLayer();
		Assertions.assertSame(n1, n2);
	}
	
	public static void getNode_notStatic(Supplier<UnitAnimationGroup> s) {
		UnitAnimationGroup v1 = s.get();
		Node n1 = v1.objectBehindLayer();
		UnitAnimationGroup v2 = s.get();
		Node n2 = v2.objectBehindLayer();
		Assertions.assertNotSame(n1, n2);
	}
	
	public static void getAttackAnimation_containsSpellAnimation(Supplier<UnitAnimationGroup> s) {
		UnitAnimationGroup v = s.get();
		Animation spellAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAttackAnimation(
			  (x) -> spellAnim
			, v.getInitializingKeyValues(Side.LEFT, Point2D.ZERO)
			, Point2D.ZERO
			, new ConsecutiveAttackDescriptor(1, 1)
			, Collections.emptySet()
			, false
		);
		
		Assertions.assertEquals(1, subAnimCount(dut, spellAnim));
	}
	
	public static void getCurrentXOffset_matchesValuePassedToInitializingKeyValues(Supplier<UnitAnimationGroup> s, double value) {
		UnitAnimationGroup v = s.get();
		Map<WritableDoubleValue, Double> map = v.getInitializingKeyValues(Side.LEFT, new Point2D(value, 0.0));
		Assertions.assertEquals(value, v.getCurrentXOffset(map));
	}
	
	
	
	public static Stream<DynamicTest> allTests(String prefix, Supplier<UnitAnimationGroup> dut) {
		return Stream.of(
			  dynamicTest(prefix + "_getNode_isStable", () -> getNode_isStable(dut))
			, dynamicTest(prefix + "_getNode_notStatic", () -> getNode_notStatic(dut))
			, dynamicTest(prefix + "_getAttackAnimation_containsSpellAnimation", () -> getAttackAnimation_containsSpellAnimation(dut))
			, dynamicTest(prefix + "_getCurrentXOffset_matchesValuePassedToInitializingKeyValues_0", () -> getCurrentXOffset_matchesValuePassedToInitializingKeyValues(dut, 0))
			, dynamicTest(prefix + "_getCurrentXOffset_matchesValuePassedToInitializingKeyValues_50", () -> getCurrentXOffset_matchesValuePassedToInitializingKeyValues(dut, 50))
			, dynamicTest(prefix + "_getCurrentXOffset_matchesValuePassedToInitializingKeyValues_-50", () -> getCurrentXOffset_matchesValuePassedToInitializingKeyValues(dut, -50))
		);
	}
	
	
	
	private static int subAnimCount(Animation haystack, Animation needle) {
		if (haystack == needle) {
			return 1;
		} else if (haystack instanceof SequentialTransition) {
			List<Animation> childs = ((SequentialTransition) haystack).getChildren();
			return childs.stream().mapToInt(x -> subAnimCount(x, needle)).sum();
		} else if (haystack instanceof ParallelTransition) {
			List<Animation> childs = ((ParallelTransition) haystack).getChildren();
			return childs.stream().mapToInt(x -> subAnimCount(x, needle)).sum();
		} else {
			return 0;
		}
	}

}
