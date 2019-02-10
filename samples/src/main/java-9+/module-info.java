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
module name.rayrobdod.fightStage.samples {
	requires name.rayrobdod.fightStage.core;
	requires name.rayrobdod.fightStage.demo;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.media;

	provides name.rayrobdod.fightStage.previewer.spi.SpellAnimationGroups
		with name.rayrobdod.fightStage.spellAnimationGroup.PreviewerSpi;
	provides name.rayrobdod.fightStage.previewer.spi.UnitAnimationGroups
		with name.rayrobdod.fightStage.unitAnimationGroup.PreviewerSpi;

	// needed so that the classes in this module can find the resources that are also in this module
	opens name.rayrobdod.fightStage.sounds;
	opens name.rayrobdod.fightStage.spellAnimationGroup;
	opens name.rayrobdod.fightStage.spellAnimationGroup.electricty;
	opens name.rayrobdod.fightStage.unitAnimationGroup;
}
