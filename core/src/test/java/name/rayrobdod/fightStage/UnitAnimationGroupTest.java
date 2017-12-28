package name.rayrobdod.fightStage;

import java.util.Collections;
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


public interface UnitAnimationGroupTest {
	
	UnitAnimationGroup getInstance();
	
	@Test
	default void getNode_isStable() {
		UnitAnimationGroup v = this.getInstance();
		Node n1 = v.getNode();
		Node n2 = v.getNode();
		Assertions.assertSame(n1, n2);
	}
	
	@Test
	default void getNode_notStatic() {
		UnitAnimationGroup v1 = this.getInstance();
		Node n1 = v1.getNode();
		UnitAnimationGroup v2 = this.getInstance();
		Node n2 = v2.getNode();
		Assertions.assertNotSame(n1, n2);
	}
	
	@Test
	default void getAttackAnimation_containsSpellAnimation() {
		UnitAnimationGroup v = this.getInstance();
		Animation spellAnim = new PauseTransition(Duration.millis(100));
		Animation dut = v.getAttackAnimation(
			  (x) -> spellAnim
			, Point2D.ZERO
			, new ConsecutiveAttackDescriptor(1, 1)
			, Collections.emptySet()
			, false
		).anim;
		
		Assertions.assertEquals(1, UnitAnimationGroupTest.subAnimCount(dut, spellAnim));
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
