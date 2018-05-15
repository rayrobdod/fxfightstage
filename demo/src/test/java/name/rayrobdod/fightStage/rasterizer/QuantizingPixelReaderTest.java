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

import java.util.HashSet;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class QuantizingPixelReaderTest {
	
	@Test
	public void blackAndWhite() {
		final QuantizingPixelReader dut = new QuantizingPixelReader(
			new ConstantPixelReader(0x44444444),
			new HashSet<>(java.util.Arrays.asList(Color.WHITE, Color.BLACK))
		);
		// then:
		Assertions.assertEquals(0xFF000000, dut.getArgb(0, 0));
	}
	
	private static final class ConstantPixelReader implements PixelReader {
		private final int backing;
		
		public ConstantPixelReader(int backing) {
			this.backing = backing;
		}
		
		public int getArgb(int x, int y) { return this.backing; }
		public Color getColor(int x, int y) { throw new UnsupportedOperationException(); }
		public javafx.scene.image.PixelFormat getPixelFormat() { throw new UnsupportedOperationException(); }
		public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) { throw new UnsupportedOperationException(); }
		public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) { throw new UnsupportedOperationException(); }
		public <T extends java.nio.Buffer> void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) { throw new UnsupportedOperationException(); }
	}
}
