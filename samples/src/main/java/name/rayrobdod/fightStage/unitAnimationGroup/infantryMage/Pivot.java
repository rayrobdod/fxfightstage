/*
 * Copyright 2019 Raymond Dodge
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
package name.rayrobdod.fightStage.unitAnimationGroup.infantryMage;

import java.util.function.Supplier;

import javafx.geometry.Point2D;

enum PivotType {
	  LeftFoot
	, RightFoot
	;

	public <A> A fold(Supplier<A> ifLeftFoot, Supplier<A> ifRightFoot) {
		switch (this) {
			case LeftFoot : return ifLeftFoot.get();
			case RightFoot : return ifRightFoot.get();
		}
		throw new IllegalStateException("Enumeration did not have legal type");
	}
}

final class Pivot {
	final PivotType typ;
	final Point2D location;
	
	public static final Pivot DEFAULT = new Pivot(PivotType.LeftFoot, Point2D.ZERO);
	
	public Pivot(PivotType typ, Point2D location) {
		this.typ = typ;
		this.location = location;
	}
}
