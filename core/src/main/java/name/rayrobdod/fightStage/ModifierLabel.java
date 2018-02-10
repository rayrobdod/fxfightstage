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

import static javafx.scene.text.FontWeight.BOLD;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * A component that displays a unit's current and maximum health
 */
final class ModifierLabel {
	
	private static final double extendedWidth = 120;
	
	private final Label node;
	private final DoubleProperty scale;
	private final DoubleProperty width;
	
	public ModifierLabel(
		  HPos alignment
	) {
		this.node = new Label();
		this.scale = new SimpleDoubleProperty(1.0);
		this.width = new SimpleDoubleProperty(0.0);
		
		this.node.borderProperty().bind(
			Bindings.solidScalableWidthBorder(
				Color.BLACK,
				3,
				(alignment == HPos.RIGHT ? 0 : 1.5),
				3,
				(alignment == HPos.LEFT ? 0 : 1.5),
				ModifierLabel.this.scale
			)
		);
		node.setBackground(BattleAnimation.solidBackground(Color.GOLD));
		node.setMinWidth(0);
		node.prefWidthProperty().bind(this.scale.multiply(this.width));
		node.setTextFill(Color.BLACK);
		node.paddingProperty().bind(Bindings.insetScale(new Insets(3, 7, 3, 7), this.scale));
		node.fontProperty().bind(Bindings.fontScale(Font.font("Sans", BOLD, 15), this.scale));
		node.setAlignment(BattleAnimation.withVCenter(alignment));
	}
	
	/**
	 * Returns the node associated with this component.
	 * The object returned has the same identity each time.
	 */
	public Node getNode() { return this.node; }
	
	/** A value that any internal related to size are multiplied by */
	public DoubleProperty scaleProperty() { return this.scale; }
	
	
	public Animation fadeInAnimation(String newText) {
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(node.textProperty(), newText, Interpolator.LINEAR),
			new KeyValue(this.width, 0, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250),
			new KeyValue(node.textProperty(), newText, Interpolator.LINEAR),
			new KeyValue(this.width, extendedWidth, Interpolator.LINEAR)
		));
		return timeline;
	}
	
	public Animation fadeOutAnimation() {
		final Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
			new KeyValue(this.width, extendedWidth, Interpolator.LINEAR)
		));
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(150),
			new KeyValue(this.width, 0, Interpolator.LINEAR)
		));
		return timeline;
	}
	
	public static Animation seqFadeInAnim(List<ModifierLabel> labels, Set<AttackModifier> mods) {
		List<AttackModifier> mods2 = mods.stream().filter(x -> x.getDisplayName().isPresent()).collect(Collectors.toList());
		ParallelTransition retval = new ParallelTransition();
		for (int i = 0; i < mods2.size(); i++) {
			Animation anim = labels.get(i).fadeInAnimation(mods2.get(i).getDisplayName().orElse(""));
			anim.setDelay(Duration.millis(150 * i));
			retval.getChildren().add(anim);
		}
		return retval;
	}
	
	public static Animation seqFadeOutAnim(List<ModifierLabel> labels, Set<AttackModifier> mods) {
		List<AttackModifier> mods2 = mods.stream().filter(x -> x.getDisplayName().isPresent()).collect(Collectors.toList());
		ParallelTransition retval = new ParallelTransition();
		for (int i = 0; i < mods2.size(); i++) {
			Animation anim = labels.get(i).fadeOutAnimation();
			retval.getChildren().add(anim);
		}
		return retval;
	}
}
