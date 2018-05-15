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
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * A PixelReader Decorator that transforms each pixel into
 * the closest value in the provided pallette
 */
final class QuantizingPixelReader implements PixelReader {
	private final javafx.scene.image.PixelReader backing;
	private final Set<Integer> pallette;
	
	public QuantizingPixelReader(PixelReader backing, Set<Color> pallette) {
		this.backing = backing;
		this.pallette = pallette.stream().map(QuantizingPixelReader::colorToArgb).collect(Collectors.toSet());
	}
	
	public int getArgb(int x, int y) {
		final int initial = backing.getArgb(x, y);
		return pallette.stream().min(argbDistanceComparator(initial)).get();
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
				final int closest = pallette.stream().min(argbDistanceComparator(initial)).get();
				if (! pallette.contains(closest)) { throw new IllegalStateException(); }
				pixelformat.setArgb(buffer, i, j, scanlineStride, closest);
			}
		}
	}
	
	private static Comparator<Integer> argbDistanceComparator(int distanceTo) {
		return Comparator.comparingDouble(subject -> distanceSquaredArgb(subject, distanceTo));
	}
	
	/** Return the distance between two argb colors in the RGB colorspace */
	private static double distanceSquaredArgb(int lhs, int rhs) {
		return ((lhs >> 24 & 0xFF) - (rhs >> 24 & 0xFF)) * ((lhs >> 24 & 0xFF) - (rhs >> 24 & 0xFF)) +
			((lhs >> 16 & 0xFF) - (rhs >> 16 & 0xFF)) * ((lhs >> 16 & 0xFF) - (rhs >> 16 & 0xFF)) +
			((lhs >> 8 & 0xFF) - (rhs >> 8 & 0xFF)) * ((lhs >> 8 & 0xFF) - (rhs >> 8 & 0xFF)) +
			((lhs >> 0 & 0xFF) - (rhs >> 0 & 0xFF)) * ((lhs >> 0 & 0xFF) - (rhs >> 0 & 0xFF));
	}
	
	private static final int colorToArgb(Color c) {
		return (((int) (c.getRed() * 255)) << 16) +
			(((int) (c.getGreen() * 255)) << 8) +
			(((int) (c.getBlue() * 255)) << 0) +
			(((int) (c.getOpacity() * 255)) << 24);
	};
}
