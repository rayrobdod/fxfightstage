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

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * A PixelReader Decorator that transforms each pixel by zeroing-out
 * the lower 3 bits of each 8-bit color channel
 */
final class TruncatingPixelReader implements PixelReader {
	private final javafx.scene.image.PixelReader backing;
	
	public TruncatingPixelReader(PixelReader backing) {
		this.backing = backing;
	}
	
	public int getArgb(int x, int y) {
		final int initial = backing.getArgb(x, y);
		return initial & 0xF8F8F8F8;
	}
	
	public Color getColor(int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	public javafx.scene.image.PixelFormat getPixelFormat() {
		throw new UnsupportedOperationException();
	}
	
	public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
		throw new UnsupportedOperationException();
	}
	
	public void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<java.nio.IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
		throw new UnsupportedOperationException();
	}
	
	public <T extends java.nio.Buffer> void getPixels(int x, int y, int w, int h, javafx.scene.image.WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) {
		backing.getPixels(x, y, w, h, pixelformat, buffer, scanlineStride);
		
		for (int i = x; i < x + w; i++) {
			for (int j = y; j < y + h; j++) {
				final int initial = pixelformat.getArgb(buffer, i, j, scanlineStride);
				final int closest = initial & 0xF8F8F8F8;
				pixelformat.setArgb(buffer, i, j, scanlineStride, closest);
			}
		}
	}
}
