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
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

final class QuantizePallette {
	private QuantizePallette() { throw new IllegalStateException("QuantizePallette is not instantiatable"); }
	
	public static Set<Color> apply(Image image, int bisectionsRemaining) {
		List<Color> imageColors =
			Stream.iterate(0, x -> x < (int) image.getWidth(), x -> x + 1).flatMap(i ->
				Stream.iterate(0, x -> x < (int) image.getHeight(), x -> x + 1).map(j ->
					image.getPixelReader().getColor(i, j)
				)
			)
			.distinct()
			// .filter(c -> c.getOpacity() >= 0.1)
			.collect(Collectors.toList());
		
		return Stream.concat(Stream.of(Color.TRANSPARENT), apply(imageColors, bisectionsRemaining)).collect(Collectors.toSet());
	}
	
	// median-cut
	static Stream<Color> apply(List<Color> colors, int bisectionsRemaining) {
		if (0 == bisectionsRemaining) {
			Color averageColor = colors.stream().collect(Collector.of(
				() -> new AveragingColor(),
				AveragingColor::add,
				AveragingColor::addAll,
				AveragingColor::result,
				Collector.Characteristics.UNORDERED
			));
			return Stream.of(averageColor);
			
		} else {
			Comparator<Color> sortByGreatestRange = colors.stream().collect(Collector.of(
				() -> new ColorRange(),
				ColorRange::add,
				ColorRange::addAll,
				ColorRange::sortByGreatestRange,
				Collector.Characteristics.UNORDERED
			));
			
			List<Color> sortedColors = colors.stream().sorted(sortByGreatestRange).collect(Collectors.toList());
			
			List<Color> left = sortedColors.stream().limit(sortedColors.size() / 2).collect(Collectors.toList());
			List<Color> right = sortedColors.stream().skip(sortedColors.size() / 2).collect(Collectors.toList());
			
			return Stream.concat(
				apply(left, bisectionsRemaining - 1),
				apply(right, bisectionsRemaining - 1)
			);
		}
	}
	
	private final static class ColorRange {
		public double minR = 1.0;
		public double maxR = 0.0;
		public double minG = 1.0;
		public double maxG = 0.0;
		public double minB = 1.0;
		public double maxB = 0.0;
		public double minA = 1.0;
		public double maxA = 0.0;
		
		public double deltaR() {return maxR - minR;}
		public double deltaG() {return maxG - minG;}
		public double deltaB() {return maxB - minB;}
		public double deltaA() {return maxA - minA;}
		
		public void add(Color color) {
			this.minR = Math.min(this.minR, color.getRed());
			this.maxR = Math.max(this.maxR, color.getRed());
			this.minG = Math.min(this.minG, color.getGreen());
			this.maxG = Math.max(this.maxG, color.getGreen());
			this.minB = Math.min(this.minB, color.getBlue());
			this.maxB = Math.max(this.maxB, color.getBlue());
			this.minA = Math.min(this.minA, color.getOpacity());
			this.maxA = Math.max(this.maxA, color.getOpacity());
		}
		public ColorRange addAll(ColorRange rhs) {
			ColorRange retval = new ColorRange();
			retval.minR = Math.min(this.minR, rhs.minR);
			retval.maxR = Math.max(this.maxR, rhs.maxR);
			retval.minG = Math.min(this.minG, rhs.minG);
			retval.maxG = Math.max(this.maxG, rhs.maxG);
			retval.minB = Math.min(this.minB, rhs.minB);
			retval.maxB = Math.max(this.maxB, rhs.maxB);
			retval.minA = Math.min(this.minA, rhs.minA);
			retval.maxA = Math.max(this.maxA, rhs.maxA);
			return retval;
		}
		
		public Comparator<Color> sortByGreatestRange() {
			final double maxDelta = Math.max(Math.max(deltaR(), deltaB()), Math.max(deltaG(), deltaA()));
			
			java.util.function.ToDoubleFunction<Color> mappingFunction = (
				maxDelta == deltaR() ? color -> color.getRed() :
				maxDelta == deltaG() ? color -> color.getGreen() :
				maxDelta == deltaB() ? color -> color.getBlue() :
				color -> color.getOpacity()
			);
			
			return Comparator.comparingDouble(mappingFunction);
		}
	}
	private final static class AveragingColor {
		public int count = 0;
		public double r = 0.0;
		public double g = 0.0;
		public double b = 0.0;
		public double a = 0.0;
		
		public void add(Color c) {
			this.count += 1;
			this.r += c.getRed();
			this.g += c.getGreen();
			this.b += c.getBlue();
			this.a += c.getOpacity();
		}
		public AveragingColor addAll(AveragingColor rhs) {
			AveragingColor retval = new AveragingColor();
			retval.count = this.count + rhs.count;
			retval.r = this.r + rhs.r;
			retval.g = this.g + rhs.g;
			retval.b = this.b + rhs.b;
			retval.a = this.a + rhs.a;
			return retval;
		}
		public Color result() {
			if (count == 0) {
				return Color.TRANSPARENT;
			} else {
				return Color.color(r / count, g / count, b / count, a / count);
			}
		}
	}
}
