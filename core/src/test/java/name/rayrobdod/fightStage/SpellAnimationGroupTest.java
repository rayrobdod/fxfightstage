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

import java.util.List;

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

public interface SpellAnimationGroupTest {
	
	SpellAnimationGroup getInstance();
	
	@Test
	default void getBackground_isStable() {
		SpellAnimationGroup v = this.getInstance();
		Node n1 = v.getBackground();
		Node n2 = v.getBackground();
		Assertions.assertSame(n1, n2);
	}
	
	@Test
	default void getBackground_notStatic() {
		SpellAnimationGroup v1 = this.getInstance();
		Node n1 = v1.getBackground();
		SpellAnimationGroup v2 = this.getInstance();
		Node n2 = v2.getBackground();
		Assertions.assertNotSame(n1, n2);
	}
	
	@Test
	default void getForeground_isStable() {
		SpellAnimationGroup v = this.getInstance();
		Node n1 = v.getForeground();
		Node n2 = v.getForeground();
		Assertions.assertSame(n1, n2);
	}
	
	@Test
	default void getForeground_notStatic() {
		SpellAnimationGroup v1 = this.getInstance();
		Node n1 = v1.getForeground();
		SpellAnimationGroup v2 = this.getInstance();
		Node n2 = v2.getForeground();
		Assertions.assertNotSame(n1, n2);
	}
	
	// @Test
	// default void getAnimation_panAnimation????() {
	// }
	
	@Test
	default void getAnimation_containsHpShakeOnce() {
		SpellAnimationGroup v = this.getInstance();
		Animation hpShakeAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAnimation(
			new Point2D(5, 5),
			new Point2D(-5, -5),
			new BattlePanAnimations(new WritableDoubleValueSink(), new WritableDoubleValueSink(), 0, 0),
			ShakeAnimationBiFunction.nil(),
			hpShakeAnim
		);
		Assertions.assertEquals(1, SpellAnimationGroupTest.subAnimCount(dut, hpShakeAnim));
	}
	
	
	public static int subAnimCount(Animation haystack, Animation needle) {
		if (haystack == needle) {
			return 1;
		} else if (haystack instanceof SequentialTransition) {
			List<Animation> childs = ((SequentialTransition) haystack).getChildren();
			return childs.stream().mapToInt(x -> SpellAnimationGroupTest.subAnimCount(x, needle)).sum();
		} else if (haystack instanceof ParallelTransition) {
			List<Animation> childs = ((ParallelTransition) haystack).getChildren();
			return childs.stream().mapToInt(x -> SpellAnimationGroupTest.subAnimCount(x, needle)).sum();
		} else {
			return 0;
		}
	}
	
	public static final class WritableDoubleValueSink implements WritableDoubleValue {
		private double backing;
		@Override public double get() {return backing;}
		@Override public void set(double v) {this.backing = v;}
		@Override public void setValue(Number v) {this.set(v.doubleValue());}
		@Override public Number getValue() {return this.get();}
	}

}
