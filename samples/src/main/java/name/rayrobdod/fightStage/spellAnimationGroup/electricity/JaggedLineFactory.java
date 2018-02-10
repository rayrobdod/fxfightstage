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
package name.rayrobdod.fightStage.spellAnimationGroup.electricty;

import java.util.List;

import javafx.geometry.Point2D;

/**
 * A function which creates a Polyline based on an "origin" point and a "target" point
 */
@FunctionalInterface
public interface JaggedLineFactory {
	
	public List<Point2D> build(
		Point2D origin,
		Point2D target
	);
}
