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
module name.rayrobdod.fightStage.demo {
	requires name.rayrobdod.fightStage.core;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.swing;

	exports name.rayrobdod.fightStage.previewer;
	exports name.rayrobdod.fightStage.previewer.spi;
	uses name.rayrobdod.fightStage.previewer.spi.UnitAnimationGroups;
	uses name.rayrobdod.fightStage.previewer.spi.SpellAnimationGroups;
}
