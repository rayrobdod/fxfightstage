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
package name.rayrobdod.fightStage;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * A collection of static functions that create bindings
 */
final class Bindings {
	private Bindings() {}
	
	private final static class InsetMultiplyBinding extends ObjectBinding<Insets> {
		private final Insets base;
		private final ObservableDoubleValue scale;
		
		public InsetMultiplyBinding(Insets base, ObservableDoubleValue scale) {
			this.base = base;
			this.scale = scale;
			super.bind(scale);
		}
		
		@Override
		protected Insets computeValue() {
			final double scaleVal = this.scale.get();
			return new Insets(
				  scaleVal * base.getTop()
				, scaleVal * base.getRight()
				, scaleVal * base.getBottom()
				, scaleVal * base.getLeft()
			);
		}
	}
	
	/**
	 * Returns a Binding whose value is an Insets with each part multiplied by the given ObservableNumberValue
	 */
	public static ObjectBinding<Insets> insetScale(Insets base, ObservableDoubleValue scale) {
		return new InsetMultiplyBinding(base, scale);
	}
	
	/**
	 * A Binding whose value is the base font, but with its size multiplied by `scale`
	 */
	public static ObjectBinding<Font> fontScale(Font base, ObservableDoubleValue scale) {
		return new ObjectBinding<Font>() {
			{
				super.bind(scale);
			}
			
			@Override
			protected Font computeValue() {
				final double scaleVal = scale.get();
				return new Font(
					base.getName(),
					scaleVal * base.getSize()
				);
			}
		};
	}
	
	/**
	 * A Binding whose value is a solid single-stroke border with the given color and scaled widths
	 */
	public static ObjectBinding<Border> solidScalableWidthBorder(Paint color, double top, double right, double bottom, double left, ObservableDoubleValue scale) {
		return new ObjectBinding<Border>() {
			{
				super.bind(scale);
			}
			
			@Override
			protected Border computeValue() {
				final double scaleVal = scale.get();
				return new Border(
					new BorderStroke(
						color,
						BorderStrokeStyle.SOLID,
						CornerRadii.EMPTY,
						new BorderWidths(
							scaleVal * top,
							scaleVal * right,
							scaleVal * bottom,
							scaleVal * left
						)
					)
				);
			}
		};
	}
	
}
