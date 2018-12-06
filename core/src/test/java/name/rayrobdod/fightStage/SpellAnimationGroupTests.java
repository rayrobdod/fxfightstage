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

import java.util.List;
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

public final class SpellAnimationGroupTests {
	private SpellAnimationGroupTests() {}
	
	public static void getBackground_isStable(Supplier<SpellAnimationGroup> s) {
		SpellAnimationGroup v = s.get();
		Node n1 = v.objectBehindLayer();
		Node n2 = v.objectBehindLayer();
		Assertions.assertSame(n1, n2);
	}
	
	public static void getBackground_notStatic(Supplier<SpellAnimationGroup> s) {
		SpellAnimationGroup v1 = s.get();
		Node n1 = v1.objectBehindLayer();
		SpellAnimationGroup v2 = s.get();
		Node n2 = v2.objectBehindLayer();
		Assertions.assertNotSame(n1, n2);
	}
	
	public static void getForeground_isStable(Supplier<SpellAnimationGroup> s) {
		SpellAnimationGroup v = s.get();
		Node n1 = v.objectFrontLayer();
		Node n2 = v.objectFrontLayer();
		Assertions.assertSame(n1, n2);
	}
	
	public static void getForeground_notStatic(Supplier<SpellAnimationGroup> s) {
		SpellAnimationGroup v1 = s.get();
		Node n1 = v1.objectFrontLayer();
		SpellAnimationGroup v2 = s.get();
		Node n2 = v2.objectFrontLayer();
		Assertions.assertNotSame(n1, n2);
	}
	
	public static void getAnimation_containsHpShakeOnce(Supplier<SpellAnimationGroup> s) {
		SpellAnimationGroup v = s.get();
		Animation hpShakeAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAnimation(
			new Point2D(5, 5),
			new Point2D(-5, -5),
			new BattlePanAnimations(new WritableDoubleValueSink(), new WritableDoubleValueSink(), 0, 0),
			ShakeAnimationBiFunction.nil(),
			hpShakeAnim
		);
		Assertions.assertEquals(1, subAnimCount(dut, hpShakeAnim));
	}
	
	
	
	public static Stream<DynamicTest> allTests(String prefix, Supplier<SpellAnimationGroup> dut) {
		return Stream.of(
			  dynamicTest(prefix + "_getBackground_isStable", () -> getBackground_isStable(dut))
			, dynamicTest(prefix + "_getBackground_notStatic", () -> getBackground_notStatic(dut))
			, dynamicTest(prefix + "_getForeground_isStable", () -> getForeground_isStable(dut))
			, dynamicTest(prefix + "_getForeground_notStatic", () -> getForeground_notStatic(dut))
			, dynamicTest(prefix + "_getAnimation_containsHpShakeOnce", () -> getAnimation_containsHpShakeOnce(dut))
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
	
	private static final class WritableDoubleValueSink implements WritableDoubleValue {
		private double backing;
		@Override public double get() {return backing;}
		@Override public void set(double v) {this.backing = v;}
		@Override public void setValue(Number v) {this.set(v.doubleValue());}
		@Override public Number getValue() {return this.get();}
	}
}
