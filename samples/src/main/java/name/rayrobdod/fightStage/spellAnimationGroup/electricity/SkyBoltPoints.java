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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;

/**
 * Creates a jagged line which
 * <ul>
 * <li>starts at the point `(target.getX, groundY)`
 * <li>each point has a smaller `y` than the preceding point (the line moves vertically upwards)
 * <li>the point's `x` may drift, but drifts less so before the line reaches `target.getY`
 * <li>Reaches as high as it needs to go offscreen
 * </ul>
 * 
 * The generation does involve a {@link java.util.Random}, so this is not a functional function
 */
public final class SkyBoltPoints implements JaggedLineFactory {
	private static final double belowSpellTarget = 50;
	private static final double maxY = -500;
	private static final double deltaXMax = 20;
	private static final double deltaXMin = -20;
	private static final double deltaYMax = 30;
	private static final double deltaYMin = 5;
	
	public List<Point2D> build(final Point2D origin, final Point2D target) {
		final Random rng = new Random();
		final List<Point2D> retval = new ArrayList<>();
		
		double currentX = target.getX();
		double currentY = target.getY() + belowSpellTarget;
		do {
			retval.add(new Point2D(currentX, currentY));
			
			final double dx = (currentY >= target.getY() ? 0.25 : 1) * (deltaXMin + (deltaXMax - deltaXMin) * rng.nextDouble());
			final double dy = deltaYMin + (deltaYMax - deltaYMin) * rng.nextDouble();
			
			currentX -= dx;
			currentY -= dy;
		} while (currentY >= maxY);
		
		return retval;
	}
}
