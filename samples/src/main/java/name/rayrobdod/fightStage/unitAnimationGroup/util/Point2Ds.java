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
package name.rayrobdod.fightStage.unitAnimationGroup.util;

import javafx.geometry.Point2D;

/**
 * Static methods related to {@link Point2D}s
 */
public final class Point2Ds {
	private Point2Ds() {}

	/**
	 * (which java version allows interfaces to have private static methods?)
	 * Returns a point on the interception of two lines (or the midpoint of p1 and p2 if there is not one unique such point)
	 * @param p1 a point on the first line
	 * @param p2 a point on the second line
	 * @param v1 the direction of the first line
	 * @param v2 the direction on the second line
	 */
	public static Point2D interception(Point2D p1, Point2D v1, Point2D p2, Point2D v2) {
		// `getX == 0` indicates a vertical line
		if (v1.getX() == 0 && v2.getX() == 0) {
			return p1.midpoint(p2);
		} else if (v1.getX() == 0) {
			final double m2 = v2.getY() / v2.getX();
			final double b2 = p2.getY() - m2 * p2.getX();
			return new Point2D(p1.getX(), m2 * p1.getX() + b2);
		} else if (v2.getX() == 0) {
			final double m1 = v1.getY() / v1.getX();
			final double b1 = p1.getY() - m1 * p1.getX();
			return new Point2D(p2.getX(), m1 * p2.getX() + b1);
		} else {
			final double m1 = v1.getY() / v1.getX();
			final double m2 = v2.getY() / v2.getX();
			final double b1 = p1.getY() - m1 * p1.getX();
			final double b2 = p2.getY() - m2 * p2.getX();

			if (m1 == m2) {
				return p1.midpoint(p2);
			} else {
				final double x = (b2 - b1) / (m1 - m2);
				final double y = m1 * x + b1;
				return new Point2D(x, y);
			}
		}
	}

	/**
	 * A Point2D representing the vector with the specified radius and angle
	 */
	public static Point2D polar(double radius, double angle) {
		return new Point2D(radius * Math.cos(angle), radius * Math.sin(angle));
	}
}
