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
package name.rayrobdod.fightStage.spellAnimationGroup;

import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.spellAnimationGroup.ElectricityShared.BasicFadeAnimation;

/**
 */
public final class Spark implements SpellAnimationGroup {
	
	private final Group background;
	private final Group foreground;
	private final BasicFadeAnimation animGenerator;
	
	public Spark() {
		this.background = new Group();
		this.foreground = new Group();
		this.animGenerator = new BasicFadeAnimation(
			ElectricityShared::chainPoints,
			this.foreground,
			this.background
		);
	}
	
	public Node getBackground() { return this.background; }
	public Node getForeground() { return this.foreground; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		Animation panAnimation,
		Animation hpAndShakeAnimation
	) {
		return animGenerator.getAnimation(
			origin,
			target,
			panAnimation,
			hpAndShakeAnimation
		);
	}
}
