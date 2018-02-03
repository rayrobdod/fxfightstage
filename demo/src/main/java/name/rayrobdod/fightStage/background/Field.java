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
package name.rayrobdod.fightStage.background;

import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public final class Field {
	
	public static Node buildGroup(Dimension2D containerSize) {
		double width = 3 * containerSize.getWidth();
		double height = 3 * containerSize.getHeight();
		double x = -1.5 * containerSize.getWidth();
		double y = -1.5 * containerSize.getHeight();
		double horizon = 0;
		
		LinearGradient lg1 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.BLACK), new Stop(1, Color.RED));
		LinearGradient lg2 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.BLUE));
		
		Rectangle background = new Rectangle(x, y, width, height);
		background.setFill(lg1);
		background.setFill(Color.CYAN);
		
		Rectangle platform = new Rectangle(x, horizon, width, height - horizon);
		platform.setFill(lg2);
		platform.setFill(Color.LAWNGREEN);
		
		return new Group(
			background,
			platform
		);
	}
}
