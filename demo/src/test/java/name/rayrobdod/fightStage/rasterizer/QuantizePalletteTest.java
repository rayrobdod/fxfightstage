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
package name.rayrobdod.fightStage.rasterizer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class QuantizePalletteTest {
	
	@Test
	public void givenBitDepth0_thenPalletteIsSizeOneAndAverageOfInputs() {
		Set<Color> expected = Stream.of(
				Color.color(0, 0.25, 0.5)
			).collect(Collectors.toSet());
		
		List<Color> inputs = Stream.of(
				Color.color(0, 0, 0),
				Color.color(0, 0.25, 0),
				Color.color(0, 0.75, 1),
				Color.color(0, 0, 1)
			).collect(Collectors.toList());
		
		Set<Color> result = QuantizePallette.apply(inputs, 0)
				.collect(Collectors.toSet());
		
		Assertions.assertEquals(expected, result);
	}
	
	@Test
	public void givenBitDepth2AndColors4_inputEqualsOutput() {
		List<Color> inputs = Stream.of(
				Color.RED,
				Color.BLUE,
				Color.GREEN,
				Color.YELLOW
			).collect(Collectors.toList());
			
		Set<Color> expected = new HashSet<>(inputs);
		
		Set<Color> result = QuantizePallette.apply(inputs, 2)
				.collect(Collectors.toSet());
		
		Assertions.assertEquals(expected, result);
	}
	
	@Test
	public void givenOneFullChannel_thenPalletteIsCenterOfQuadrants() {
		final int bitDepth = 1;
		double[] expected = {0.25, 0.75};
		double[] result = QuantizePallette.apply(
				Stream.iterate(0d, i -> i + 1d / 255d).limit(256).map(r -> Color.color(r, 0, 0)).collect(Collectors.toList()),
				bitDepth
			)
			.mapToDouble(Color::getRed)
			.toArray();
		// then:
		Assertions.assertArrayEquals(expected, result, 1d / 512d);
	}
	
	@Test
	public void givenThreeFullChannels_thenResultsAreEquallySpacedOctants() {
		final int bitDepth = 6;
		final List<Color> expected =
			Stream.of(0.125, 0.375, 0.625, 0.875).flatMap(r ->
				Stream.of(0.125, 0.375, 0.625, 0.875).flatMap(g ->
					Stream.of(0.125, 0.375, 0.625, 0.875).map(b ->
						Color.color(r, g, b)
					)
				)
			)
			.sorted(colorComparator())
			.collect(Collectors.toList());
		
		final List<Color> result =
			QuantizePallette.apply(
				Stream.iterate(0d, i -> i + 1d / 127d).limit(128).flatMap(r ->
					Stream.iterate(0d, i -> i + 1d / 127d).limit(128).flatMap(g ->
						Stream.iterate(0d, i -> i + 1d / 127d).limit(128).map(b ->
							Color.color(r, g, b)
						)
					)
				).collect(Collectors.toList()),
				bitDepth
			)
			.sorted(colorComparator())
			.collect(Collectors.toList());

		// then:
		Assertions.assertEquals(expected.size(), result.size(), "Expected and Result had different size");
		for (int i = 0; i < result.size(); i++) {
			final int i2 = i;
			Assertions.assertTrue(
				colorEqualsDelta(expected.get(i), result.get(i), 1d / 256d),
				() -> "" + expected.get(i2) + " != " + result.get(i2)
			);
		}
	}
	
	@Test
	public void givenLessThan16Colors_givenBitDepthOf4_thenPalletteContainsAllInputColors() {
		List<Color> expected =
			Stream.of(
				Color.TRANSPARENT, Color.RED, Color.LIME, Color.BLUE,
				Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.BLACK
			)
			.sorted(colorComparator())
			.collect(Collectors.toList());
		
		List<Color> result = QuantizePallette.apply(expected, 4)
			.distinct()
			.sorted(colorComparator())
			.collect(Collectors.toList());
		
		// then:
		Assertions.assertEquals(expected.size(), result.size(), () -> "Expected and Result had different size: " + result);
		for (int i = 0; i < result.size(); i++) {
			final int i2 = i;
			Assertions.assertTrue(
				colorEqualsDelta(expected.get(i), result.get(i), 1d / 256d),
				() -> "" + expected.get(i2) + " != " + result.get(i2)
			);
		}
	}
	
	private static final int colorToArgb(Color c) {
		return (((int) (c.getRed() * 255)) << 16) +
			(((int) (c.getGreen() * 255)) << 8) +
			(((int) (c.getBlue() * 255)) << 0) +
			(((int) (c.getOpacity() * 255)) << 24);
	};
	
	private static final class GeneratorPixelReader implements PixelReader {
		private final IntBinaryOperator backing;
		public GeneratorPixelReader(IntBinaryOperator backing) {
			this.backing = backing;
		}
		
		public int getArgb(int x, int y) { throw new UnsupportedOperationException(); }
		public Color getColor(int x, int y) { throw new UnsupportedOperationException(); }
		public javafx.scene.image.PixelFormat getPixelFormat() { throw new UnsupportedOperationException(); }
		public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) { throw new UnsupportedOperationException(); }
		public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) { throw new UnsupportedOperationException(); }
		public <T extends java.nio.Buffer> void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) {
			for (int i = x; i < x + w; i++) {
				for (int j = y; j < y + h; j++) {
					pixelformat.setArgb(buffer, i, j, scanlineStride, backing.applyAsInt(i, j));
				}
			}
		}
	}
	
	private static final Comparator<Color> colorComparator() {
		Comparator<Color> red = Comparator.comparingDouble(x -> x.getRed());
		Comparator<Color> green = Comparator.comparingDouble(x -> x.getGreen());
		Comparator<Color> blue = Comparator.comparingDouble(x -> x.getBlue());
		Comparator<Color> alpha = Comparator.comparingDouble(x -> x.getOpacity());
		
		return red.thenComparing(green).thenComparing(blue).thenComparing(alpha);
	}
	
	private static final boolean colorEqualsDelta(Color lhs, Color rhs, double delta) {
		return (
			Math.abs(lhs.getRed() - rhs.getRed()) < delta &&
			Math.abs(lhs.getGreen() - rhs.getGreen()) < delta &&
			Math.abs(lhs.getBlue() - rhs.getBlue()) < delta &&
			Math.abs(lhs.getOpacity() - rhs.getOpacity()) < delta
		);
	}
}
