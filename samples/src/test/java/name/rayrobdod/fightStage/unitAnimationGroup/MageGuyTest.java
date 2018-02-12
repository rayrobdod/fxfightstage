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
package name.rayrobdod.fightStage.unitAnimationGroup;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.framework.junit5.ApplicationExtension;

import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroupTest;

@ExtendWith(ApplicationExtension.class)
@Tag("image")
public class MageGuyTest implements UnitAnimationGroupTest {
	public UnitAnimationGroup getInstance() {
		return new MageGuy();
	}
}
