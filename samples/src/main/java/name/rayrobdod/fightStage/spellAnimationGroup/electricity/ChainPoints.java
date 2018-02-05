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
 * <li>starts at the point `origin`
 * <li>ends at the point `target`
 * <li>doesn't drift too far from the straight line segment connecting origin and target
 * <li>
 * </ul>
 * 
 * The generation does involve a {@link java.util.Random}, so this is not a functional function
 */
public final class ChainPoints implements JaggedLineFactory {
	private static final double chainDeltaParMax = 30;
	private static final double chainDeltaParMin = 10;
	private static final double chainPerpMaxValue = 30;
	private static final double chainPerpMinValue = -10;
	
	public List<Point2D> build(final Point2D origin, final Point2D target) {
		final Random rng = new Random();
		final List<Point2D> retval = new ArrayList<>();
		
		final double distance = origin.distance(target);
		final Point2D parUnitVector = target.subtract(origin).normalize();
		final Point2D perpUnitVector = new Point2D(-parUnitVector.getY(), parUnitVector.getX()).normalize();
		
		retval.add(origin);
		
		double currentParallel = chainPointsParDelta(rng, distance);
		double perpPolarity = (rng.nextBoolean() ? 1.0 : -1.0);
		while (currentParallel < 1.0) {
			final double perp = perpPolarity * (chainPerpMinValue + (chainPerpMaxValue - chainPerpMinValue) * rng.nextDouble());
			
			retval.add(origin.add(parUnitVector.multiply(distance * currentParallel)).add(perpUnitVector.multiply(perp)));
			
			currentParallel += chainPointsParDelta(rng, distance);
			perpPolarity *= -1.0;
		}
		
		retval.add(target);
		
		return retval;
	}
	
	private static double chainPointsParDelta(Random rng, double distance) {
		return (chainDeltaParMin + (chainDeltaParMax - chainDeltaParMin) * rng.nextDouble()) / distance;
	}
}
