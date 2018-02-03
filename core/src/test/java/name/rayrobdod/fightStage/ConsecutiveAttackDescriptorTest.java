package name.rayrobdod.fightStage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ConsecutiveAttackDescriptorTest {
	
	@Test
	public void when11_thenIsOnly() {
		ConsecutiveAttackDescriptor dut = new ConsecutiveAttackDescriptor(1, 1);
		Assertions.assertTrue(dut.isOnly());
	}
	
	@Test
	public void when11_thenIsFirst() {
		ConsecutiveAttackDescriptor dut = new ConsecutiveAttackDescriptor(1, 1);
		Assertions.assertTrue(dut.isFirst());
	}
	
	@Test
	public void when11_thenIsLast() {
		ConsecutiveAttackDescriptor dut = new ConsecutiveAttackDescriptor(1, 1);
		Assertions.assertTrue(dut.isLast());
	}
	
}
