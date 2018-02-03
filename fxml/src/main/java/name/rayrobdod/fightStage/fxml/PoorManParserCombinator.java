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
package name.rayrobdod.fightStage.fxml;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

// FastParse can apparently get a sensible failure message. I have not figured out how.
// Hence, this does not produce a sensible failure message for many cases.

final class PoorManParserCombinator {
	/** A tuple of two arbitrary values */
	public static final class Tuple2<A,B> {
		public final A _1;
		public final B _2;
		public Tuple2(A _1, B _2) {this._1 = _1; this._2 = _2;}
		public <C> C zip(BiFunction<A, B, C> mapping) {return mapping.apply(_1, _2);}
		@Override public boolean equals(Object rhs) {
			if (rhs instanceof Tuple2) {
				Tuple2 rhs2 = (Tuple2) rhs;
				return java.util.Objects.equals(this._1, rhs2._1) &&
					java.util.Objects.equals(this._2, rhs2._2);
			} else {
				return false;
			}
		}
		@Override public int hashCode() {return java.util.Objects.hash(_1, _2);}
		@Override public String toString() {return "(" + _1 + ", " + _2 + ")";}
	}
	
	public static final class ParseResult<A> {
		public final boolean isSuccess;
		/** Only valid if `isSuccess` is true */
		public final A value;
		public final int charsRead;
		/** Only valid if `isSuccess` is false */
		public final List<String> expecting;
		
		private ParseResult(boolean isSuccess, A value, int charsRead, List<String> expecting) {
			this.isSuccess = isSuccess;
			this.value = value;
			this.charsRead = charsRead;
			this.expecting = expecting;
		}
		
		public static <A> ParseResult<A> success(A value, int charsRead) { return new ParseResult<A>(true, value, charsRead, java.util.Collections.emptyList()); }
		public static <A> ParseResult<A> failure(String expecting) { return ParseResult.failure(java.util.Collections.singletonList(expecting)); }
		public static <A> ParseResult<A> failure(List<String> expecting) { return ParseResult.failure(expecting, 0); }
		public static <A> ParseResult<A> failure(List<String> expecting, int charsRead) { return new ParseResult<A>(false, null, charsRead, expecting); }
		
		public <B> ParseResult<B> map(Function<A, B> mapping) {
			if (this.isSuccess) {
				return ParseResult.success(mapping.apply(this.value), this.charsRead);
			} else {
				return ParseResult.failure(this.expecting, this.charsRead);
			}
		}
		
		public ParseResult<A> addCharsRead(int value) { return new ParseResult<A>(this.isSuccess, this.value, this.charsRead + value, this.expecting);}
		
		@Override public boolean equals(Object rhs) {
			if (rhs instanceof ParseResult) {
				ParseResult rhs2 = (ParseResult) rhs;
				return this.isSuccess == rhs2.isSuccess &&
					java.util.Objects.equals(this.value, rhs2.value) &&
					java.util.Objects.equals(this.expecting, rhs2.expecting) &&
					this.charsRead == rhs2.charsRead;
			} else {
				return false;
			}
		}
		
		@Override public int hashCode() {
			return java.util.Objects.hash(isSuccess, value, expecting, charsRead);
		}
		
		@Override public String toString() {
			return "ParseResult[isSuccess = " + this.isSuccess +
				"; charsRead = " + charsRead +
				"; value = " + value +
				"; expecting = " + expecting +
				"]";
		}
	}
	
	@FunctionalInterface
	public static interface Parser<A> {
		public ParseResult<A> parse(String input);
		default <B> Parser<B> map(Function<A, B> mapping) {
			return (input) -> Parser.this.parse(input).map(mapping);
		}
		default Parser<List<A>> rep() { return this.rep(0); }
		default Parser<List<A>> rep(int min) { return this.rep(min, Integer.MAX_VALUE); }
		default Parser<List<A>> rep(int min, int max) { return new Repeat<A>(this, min, max); }
		default <B> Parser<Tuple2<A, B>> andThen(Parser<B> rhs) { return new AndThen<>(this, rhs); }
		default Parser<A> orElse(Parser<A> rhs) { return new OrElse<>(this, rhs); }
		default Parser<A> optionally(A defaultValue) { return new OptionallyWithDefault<>(this, defaultValue); }
		default Parser<A> opaque(String expectingMessage) { return new Opaque<>(this, expectingMessage); }
		default Parser<A> log(String label) { return new Log<>(this, label); }
	}
	
	private static final java.util.stream.Collector<Character, ?, String> charsToString =
			java.util.stream.Collectors.mapping(c -> "" + c, java.util.stream.Collectors.joining());
	
	@FunctionalInterface
	public static interface CharParser extends Parser<Character> {
		default Parser<String> repAsString() {
			return this.rep().map(x -> x.stream().collect(charsToString));
		}
		default Parser<String> repAsString(int min) {
			return this.rep(min).map(x -> x.stream().collect(charsToString));
		}
		default Parser<String> repAsString(int min, int max) {
			return this.rep(min, max).map(x -> x.stream().collect(charsToString));
		}
	}
	
	/** Used to allow mutually recursive parsers */
	public static final class DelayedConstructionParser<A> implements Parser<A> {
		private final Supplier<Parser<A>> inner;
		public DelayedConstructionParser(Supplier<Parser<A>> inner) {this.inner = inner;}
		public ParseResult<A> parse(String input) {
			return inner.get().parse(input);
		}
	}
	
	/** Only succeeds at the end of the input */
	public static final class End implements Parser<Void> {
		public End() {}
		public ParseResult<Void> parse(String input) {
			if ("".equals(input)) {
				return ParseResult.success(null, 0);
			} else {
				return ParseResult.failure("End of Input");
			}
		}
	}
	
	public static final class CharIn implements CharParser {
		private final String allowed;
		public CharIn(String allowed) {this.allowed = allowed;}
		public ParseResult<Character> parse(String input) {
			if (input.length() < 1) {
				return ParseResult.failure(this.expecting());
			} else if (0 <= allowed.indexOf(input.charAt(0))) {
				return ParseResult.success(input.charAt(0), 1);
			} else {
				return ParseResult.failure(this.expecting());
			}
		}
		private List<String> expecting() {
			return allowed.chars().boxed()
				.map(x -> "`" + ((char)(int) x) + "`")
				.collect(java.util.stream.Collectors.toList());
		}
	}
	
	public static final class IsString implements Parser<String> {
		private final String toMatch;
		public IsString(String toMatch) {this.toMatch = toMatch;}
		public ParseResult<String> parse(String input) {
			if (input.length() < toMatch.length()) {
				return ParseResult.failure("`" + toMatch + "`");
			} else if (toMatch.equals(input.substring(0, toMatch.length()))) {
				return ParseResult.success(toMatch, toMatch.length());
			} else {
				return ParseResult.failure("`" + toMatch + "`");
			}
		}
	}
	
	private static final class Repeat<A> implements Parser<List<A>> {
		private final Parser<A> inner;
		private final int min;
		private final int max;
		public Repeat(Parser<A> inner, int min, int max) {this.inner = inner; this.min = min; this.max = max;}
		public ParseResult<List<A>> parse(String input) {
			int loopCounter = 0;
			int charsRead = 0;
			final List<A> values = new java.util.ArrayList<>();
			ParseResult<A> innerRes = null;
			
			while (loopCounter < max) {
				innerRes = inner.parse(input);
				
				if (innerRes.isSuccess) {
					input = input.substring(innerRes.charsRead);
					values.add(innerRes.value);
					charsRead += innerRes.charsRead;
					loopCounter++;
				} else {
					break;
				}
			}
			
			if (min <= loopCounter && loopCounter <= max) {
				return ParseResult.success(values, charsRead);
			} else {
				return ParseResult.failure(this.expectingString(innerRes.expecting, min, max));
			}
		}
		private static String expectingString(List<String> innerExpecting, int min, int max) {
			String prefix = (min == 0 && max == Integer.MAX_VALUE ? "Zero or more" :
				(min == 0 && max == 1 ? "Zero or one" :
				(min == 1 && max == Integer.MAX_VALUE ? "One or more" :
				(max == Integer.MAX_VALUE ? min + " or more" :
					"Between " + min + " and " + max
				))));
			String suffix = (innerExpecting.size() == 0 ? "???" :
				(innerExpecting.size() == 1 ? innerExpecting.get(0) :
					innerExpecting.stream().collect(java.util.stream.Collectors.joining(", ", "<", ">"))
				));
			return prefix + " copies of " + suffix;
		}
	}
	
	private static final class AndThen<A, B> implements Parser<Tuple2<A, B>> {
		private final Parser<A> left;
		private final Parser<B> right;
		public AndThen(Parser<A> left, Parser<B> right) {this.left = left; this.right = right;}
		public ParseResult<Tuple2<A, B>> parse(String input) {
			ParseResult<A> leftRes = left.parse(input);
			if (leftRes.isSuccess) {
				String input2 = input.substring(leftRes.charsRead);
				ParseResult<B> rightRes = right.parse(input2);
					
				if (rightRes.isSuccess) {
					return ParseResult.success(new Tuple2<>(leftRes.value, rightRes.value), leftRes.charsRead + rightRes.charsRead);
				} else {
					List<String> expecting = new java.util.ArrayList<>(leftRes.expecting.size() + rightRes.expecting.size());
					expecting.addAll(leftRes.expecting);
					expecting.addAll(rightRes.expecting);
					
					return ParseResult.failure(expecting, leftRes.charsRead);
				}
			} else {
				return ParseResult.failure(leftRes.expecting);
			}
		}
	}
	
	private static final class OrElse<A> implements Parser<A> {
		private final Parser<A> left;
		private final Parser<A> right;
		public OrElse(Parser<A> left, Parser<A> right) {this.left = left; this.right = right;}
		public ParseResult<A> parse(String input) {
			ParseResult<A> leftRes = left.parse(input);
			if (leftRes.isSuccess) {
				return leftRes;
			} else {
				ParseResult<A> rightRes = right.parse(input);
				if (rightRes.isSuccess) {
					return rightRes;
				} else {
					List<String> expecting = new java.util.ArrayList<>(leftRes.expecting.size() + rightRes.expecting.size());
					expecting.addAll(leftRes.expecting);
					expecting.addAll(rightRes.expecting);
					
					return ParseResult.failure(expecting);
				}
			}
		}
	}
	
	private static final class OptionallyWithDefault<A> implements Parser<A> {
		private final Parser<A> inner;
		private final A defaultValue;
		public OptionallyWithDefault(Parser<A> inner, A defaultValue) {this.inner = inner; this.defaultValue = defaultValue;}
		public ParseResult<A> parse(String input) {
			ParseResult<A> innerRes = inner.parse(input);
			if (innerRes.isSuccess) {
				return innerRes;
			} else {
				return ParseResult.success(defaultValue, 0);
			}
		}
	}
	
	private static final class Opaque<A> implements Parser<A> {
		private final Parser<A> inner;
		private final String expectingStr;
		public Opaque(Parser<A> inner, String expectingStr) {this.inner = inner; this.expectingStr = expectingStr;}
		public ParseResult<A> parse(String input) {
			ParseResult<A> innerRes = inner.parse(input);
			if (innerRes.isSuccess) {
				return ParseResult.success(innerRes.value, innerRes.charsRead);
			} else {
				return ParseResult.failure(java.util.Arrays.asList(expectingStr), innerRes.charsRead);
			}
		}
	}
	
	private static final class Log<A> implements Parser<A> {
		private static int level = 0;
		private final Parser<A> inner;
		private final String label;
		public Log(Parser<A> inner, String label) {this.inner = inner; this.label = label;}
		public ParseResult<A> parse(String input) {
			for (int i = 0; i < level; i++) {System.out.print("    ");}
			level++;
			System.out.println("+ " + label + " : " + (input.length() >= 15 ? input.substring(0, 15) + "..." : input));
			ParseResult<A> retval = inner.parse(input);
			level--;
			for (int i = 0; i < level; i++) {System.out.print("    ");}
			System.out.println("- " + label + " : " + retval);
			return retval;
		}
	}
	
}
