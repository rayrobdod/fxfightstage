package name.rayrobdod.femp_fx_demo.images;

import java.util.List;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
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
	
	@Test
	default void getAnimation_containsPanOnce() {
		SpellAnimationGroup v = this.getInstance();
		Animation panAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAnimation(
			new Point2D(5, 5),
			new Point2D(-5, -5),
			panAnim,
			new PauseTransition(Duration.millis(50))
		);
		Assertions.assertEquals(1, SpellAnimationGroupTest.subAnimCount(dut, panAnim));
	}
	
	@Test
	default void getAnimation_containsHpShakeOnce() {
		SpellAnimationGroup v = this.getInstance();
		Animation hpShakeAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAnimation(
			new Point2D(5, 5),
			new Point2D(-5, -5),
			new PauseTransition(Duration.millis(50)),
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

}
