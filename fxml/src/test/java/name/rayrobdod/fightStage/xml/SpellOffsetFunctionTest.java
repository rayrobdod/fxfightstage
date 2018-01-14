package name.rayrobdod.fightStage.fxml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import name.rayrobdod.fightStage.fxml.FxmlSpellAnimationGroup.OffsetFunction;


public class SpellOffsetFunctionTest {
	
	@Test
	public void constant_25() {
		OffsetFunction dut = OffsetFunction.valueOf("25");
		Assertions.assertEquals(25, dut.apply(50,50,50,50));
		Assertions.assertEquals(25, dut.apply(-50,-50,-50,-50));
	}
	
	@Test
	public void constant_3p14() {
		OffsetFunction dut = OffsetFunction.valueOf("3.14");
		Assertions.assertEquals(3.14, dut.apply(50,50,50,50));
		Assertions.assertEquals(3.14, dut.apply(-50,-50,-50,-50));
	}
	
	@Test
	public void originX() {
		OffsetFunction dut = OffsetFunction.valueOf("originX");
		Assertions.assertEquals(-25, dut.apply(-25,50,50,50));
		Assertions.assertEquals(25, dut.apply(25,-50,-50,-50));
	}
	
	@Test
	public void originxPlus10() {
		OffsetFunction dut = OffsetFunction.valueOf("originX+10");
		Assertions.assertEquals(-15, dut.apply(-25,Math.PI,Math.PI,Math.PI));
		Assertions.assertEquals(35, dut.apply(25,Math.PI,Math.PI,Math.PI));
	}
	
	@Test
	public void averageXs() {
		OffsetFunction dut = OffsetFunction.valueOf("(originX+targetX)/2");
		Assertions.assertEquals(40, dut.apply(30,Math.PI,50,Math.PI));
		Assertions.assertEquals(10, dut.apply(35,Math.PI,-15,Math.PI));
	}
	
	@Test
	public void orderOfOperations_mp() {
		OffsetFunction dut = OffsetFunction.valueOf("6-3+3");
		Assertions.assertEquals(6-3+3, dut.apply(Math.PI,Math.PI,Math.PI,Math.PI));
	}
	
	@Test
	public void orderOfOperations_mt() {
		OffsetFunction dut = OffsetFunction.valueOf("100-4*3");
		Assertions.assertEquals(100-4*3, dut.apply(Math.PI,Math.PI,Math.PI,Math.PI));
	}
	
	@Test
	public void illegalArgument_asdf() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
			OffsetFunction.valueOf("asdf")
		);
	}
	
	@Test
	public void illegalArgument_1p2pX() {
		IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
			OffsetFunction.valueOf("1+2+x")
		);
	}
	
	@Test
	public void illegalArgument_origin() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			OffsetFunction.valueOf("origin")
		);
	}
	
	@Test
	public void illegalArgument_consecutivePlusSigns() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			OffsetFunction.valueOf("+++")
		);
	}
}
