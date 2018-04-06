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

import java.util.stream.Collectors;

import javafx.geometry.HPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.matcher.control.TextMatchers;

@ExtendWith(ApplicationExtension.class)
@Tag("image")
public final class HealthBarTest {
	
	@Test
	public void initialCurrentHealthMatchesConstructorValue() {
		final int expected = 40;
		HealthBar dut = new HealthBar(
			HPos.LEFT,
			Color.ORANGE,
			expected,
			60
		);
		Assertions.assertEquals(expected, dut.currentHealthProperty().get());
	}
	
	@Test
	public void initialMaximumHealthMatchesConstructorValue() {
		final int expected = 60;
		HealthBar dut = new HealthBar(
			HPos.LEFT,
			Color.ORANGE,
			20,
			expected
		);
		Assertions.assertEquals(expected, dut.maximumHealthProperty().get());
	}
	
	@Test
	public void whenCurrentHealthIsSet_thenLabelsTextUpdates() {
		final int expected = 50;
		HealthBar dut = new HealthBar(HPos.LEFT, Color.ORANGE, 40, 60);
		dut.currentHealthProperty().set(expected);
		FxAssert.verifyThat(
			(javafx.scene.text.Text) dut.getNode().lookup(".label"),
			TextMatchers.hasText("" + expected)
		);
	}
	
	@Test
	public void whenCurrentHealthIsSet_thenNotchFillUpdates() {
		final int expected = 50;
		HealthBar dut = new HealthBar(
			HPos.LEFT,
			Color.ORANGE,
			40,
			60
		);
		dut.currentHealthProperty().set(expected);
		Assertions.assertEquals(
			(long) expected,
			(long) dut.getNode().lookupAll(".healthbar-notch").stream()
				.filter(x -> ((Shape) x).getFill().equals(Color.LIME))
				.collect(Collectors.counting())
		);
	}
	
	@Test
	public void whenMaximumHealthIsSet_thenNotchVisibleUpdates() {
		final int expected = 50;
		HealthBar dut = new HealthBar(
			HPos.LEFT,
			Color.ORANGE,
			40,
			60
		);
		dut.maximumHealthProperty().set(expected);
		Assertions.assertEquals(
			(long) expected,
			(long) dut.getNode().lookupAll(".healthbar-notch").stream()
				.filter(x -> x.isVisible())
				.collect(Collectors.counting())
		);
	}
}
