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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ConsecutiveAttackDescriptorTest {
	
	@Test
	public void when11_thenIsOnly() {
		ConsecutiveAttackDescriptor dut = new ConsecutiveAttackDescriptor(1, 1);
		Assertions.assertTrue(dut.isOnly());
	}
	
	@Test
	public void when11_thenIsFirst() {
		ConsecutiveAttackDescriptor dut = new ConsecutiveAttackDescriptor(1, 1);
		Assertions.assertTrue(dut.isFirst());
	}
	
	@Test
	public void when11_thenIsLast() {
		ConsecutiveAttackDescriptor dut = new ConsecutiveAttackDescriptor(1, 1);
		Assertions.assertTrue(dut.isLast());
	}
	
}
