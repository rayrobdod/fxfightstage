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

import javafx.animation.Animation;
import javafx.util.Duration;

public interface ShakeAnimationBiFunction {
	public Animation apply();
	public Animation apply(double intensity);
	public Animation apply(Duration duration);
	public Animation apply(double intensity, Duration duration);
	
	public static ShakeAnimationBiFunction nil() {
		return new ShakeAnimationBiFunction() {
			public Animation apply() {return Animations.nil();}
			public Animation apply(double intensity) {return Animations.nil();}
			public Animation apply(Duration duration) {return Animations.nil();}
			public Animation apply(double intensity, Duration duration) {return Animations.nil();}
		};
	}
}
