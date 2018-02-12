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

/**
 * Represents a skill that changes the properties of an attack.
 * 
 * There are definitely other properties that an AttackModifier could have,
 * such as how the AttackModifier modifies an attack, but the battle
 * animation doesn't care about those properties.
 */
public final class AttackModifier {
	/**
	 * The name to display when this skill is activated.
	 */
	public final String displayName;
	
	public AttackModifier(
		String displayName
	) {
		this.displayName = displayName;
	}
}
