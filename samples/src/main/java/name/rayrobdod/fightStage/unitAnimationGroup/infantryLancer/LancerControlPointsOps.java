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
package name.rayrobdod.fightStage.unitAnimationGroup.infantryLancer;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import javafx.geometry.Point2D;

/**
 * Partial Classes! :D
 */
interface LancerControlPointsOps {
	LancerControlPoints self();
	LancerControlPoints zipMap(LancerControlPoints rhs, BinaryOperator<Point2D> op);
	LancerControlPoints map(UnaryOperator<Point2D> op);
	
	default LancerControlPoints add(LancerControlPoints rhs) {
		return this.zipMap(rhs, Point2D::add);
	}
	default LancerControlPoints multiply(int factor) {
		return this.map(s -> s.multiply(factor));
	}
	
	default Point2D lanceTip() {
		final Point2D center = self().lanceCenter;
		final Point2D direction = center.subtract(self().lanceControl).normalize();
		
		return center.add(direction.multiply(70));
	}
}
