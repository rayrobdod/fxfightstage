package name.rayrobdod.fightStage.fxml;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import name.rayrobdod.fightStage.fxml.PoorManParserCombinator.*;


public class PoorManParserCombinatorTest {
	
	@Test
	public void charInAbc_a() {
		Parser<Character> dut = new CharIn("abc");
		Assertions.assertEquals(ParseResult.success('a', 1), dut.parse("a"));
	}
	
	@Test
	public void charInAbc_1() {
		Parser<Character> dut = new CharIn("abc");
		Assertions.assertEquals(ParseResult.failure(Arrays.asList("`a`", "`b`", "`c`")), dut.parse("1"));
	}
	
	@Test
	public void charInAbc_bc() {
		Parser<Character> dut = new CharIn("abc");
		Assertions.assertEquals(ParseResult.success('b', 1), dut.parse("bc"));
	}
	
	@Test
	public void charInAbc_empty() {
		Parser<Character> dut = new CharIn("abc");
		Assertions.assertEquals(ParseResult.failure(Arrays.asList("`a`", "`b`", "`c`")), dut.parse(""));
	}
	
	@Test
	public void repeatCharInAbc_aabb() {
		Parser<String> dut = new CharIn("abc").repAsString();
		Assertions.assertEquals(ParseResult.success("aabb", 4), dut.parse("aabb"));
	}
	
	@Test
	public void repeatCharMax5_aaaa() {
		Parser<String> dut = new CharIn("a").repAsString(0, 5);
		Assertions.assertEquals(ParseResult.success("aaaa", 4), dut.parse("aaaa"));
	}
	
	@Test
	public void repeatCharMax5_aaaaaa() {
		Parser<String> dut = new CharIn("a").repAsString(0, 5);
		Assertions.assertEquals(ParseResult.success("aaaaa", 5), dut.parse("aaaaaa"));
	}
	
	@Test
	public void repeatCharInDigit_42p16() {
		Parser<String> dut = new CharIn("0123456789").repAsString();
		Assertions.assertEquals(ParseResult.success("42", 2), dut.parse("42+16"));
	}
	
	@Test
	public void isString_exact() {
		Parser<String> dut = new IsString("12345");
		Assertions.assertEquals(ParseResult.success("12345", 5), dut.parse("12345"));
	}
	
	@Test
	public void isString_long() {
		Parser<String> dut = new IsString("12345");
		Assertions.assertEquals(ParseResult.success("12345", 5), dut.parse("1234567"));
	}
	
	@Test
	public void isString_short() {
		Parser<String> dut = new IsString("12345");
		Assertions.assertEquals(ParseResult.failure("`12345`"), dut.parse("123"));
	}
	
	@Test
	public void isString_diverging() {
		Parser<String> dut = new IsString("12345");
		Assertions.assertEquals(ParseResult.failure("`12345`"), dut.parse("123ab"));
	}
	
	@Test
	public void isString_emptyPattern() {
		Parser<String> dut = new IsString("");
		Assertions.assertEquals(ParseResult.success("", 0), dut.parse("12345"));
	}
	
	@Test
	public void integerMapping_123() {
		Parser<String> numStr = new CharIn("0123456789").repAsString();
		Parser<Integer> numNum = numStr.map(Integer::parseInt);
		Assertions.assertEquals(ParseResult.success("123", 3), numStr.parse("123"));
		Assertions.assertEquals(ParseResult.success(123, 3), numNum.parse("123"));
	}
	
	@Test
	public void andThen_aaa111() {
		Parser<Tuple2<String, String>> dut = new CharIn("abc").repAsString(1).andThen(new CharIn("123").repAsString(1));
		Assertions.assertEquals(ParseResult.success(new Tuple2<>("aaa", "111"), 6), dut.parse("aaa111"));
	}
	
	@Test
	public void andThen_aaa() {
		Parser<Tuple2<String, String>> dut = new CharIn("abc").repAsString(1).andThen(new CharIn("123").repAsString(1));
		Assertions.assertEquals(ParseResult.failure(Arrays.asList("One or more copies of <`1`, `2`, `3`>"), 3), dut.parse("aaa"));
	}
	
	@Test
	public void orElse_aabbaabb() {
		Parser<String> dut = new CharIn("a").repAsString(1).orElse(new CharIn("b").repAsString(1));
		Assertions.assertEquals(ParseResult.success("aa", 2), dut.parse("aabbaabb"));
	}
	
	@Test
	public void orElse_bbaabbaa() {
		Parser<String> dut = new CharIn("a").repAsString(1).orElse(new CharIn("b").repAsString(1));
		Assertions.assertEquals(ParseResult.success("bb", 2), dut.parse("bbaabbaa"));
	}
	
}
