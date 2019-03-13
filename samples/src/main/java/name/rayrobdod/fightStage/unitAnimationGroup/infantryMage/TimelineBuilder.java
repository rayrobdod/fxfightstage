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

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

final class TimelineBuilder {
	Duration currentTime;
	final WritableBoneControls keys;
	BoneControls currentValues;
	final ArrayList<KeyFrame> timeline;
	
	public TimelineBuilder(WritableBoneControls keys, BoneControls initialValues) {
		this.currentTime = Duration.ZERO;
		this.currentValues = initialValues;
		this.keys = keys;
		this.timeline = new ArrayList<>();
		this.timeline.add(keys.createKeyFrame(currentTime, currentValues));
	}
	
	public void append(Duration deltaTime, BoneControlsOptional deltaValues) {
		currentTime = currentTime.add(deltaTime);
		currentValues = currentValues.copyWith(deltaValues);
		timeline.add(keys.createKeyFrame(currentTime, deltaValues));
	}
	
	public final class BuildResult {
		public final Timeline timeline;
		public final BoneControls endValues;
		public BuildResult(Timeline timeline, BoneControls endValues) {
			this.timeline = timeline;
			this.endValues = endValues;
		}
	}
	
	public Timeline build() {
		currentTime = currentTime.add(Duration.ONE);
		timeline.add(keys.createKeyFrame(currentTime, currentValues));
		return new Timeline(timeline.toArray(new KeyFrame[0]));
	}
	
	public BoneControls currentValues() {
		return currentValues;
	}
}
