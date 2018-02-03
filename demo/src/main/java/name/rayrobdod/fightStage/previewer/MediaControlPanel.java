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
package name.rayrobdod.fightStage.previewer;


import javafx.animation.Animation;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * A panel which control the playing of an animation
 */
public final class MediaControlPanel {
	
	private final GridPane node;
	
	/**
	 * @param animationProperty the animation that this's actions act upon
	 * @param playButtonEvent the action that is to occur upon
	 */
	public MediaControlPanel(
		  final ObjectProperty<Animation> animationProperty
		, final EventHandler<ActionEvent> playButtonEvent
	) {
		final ProgressBar progress = new ProgressBar(0.0); {
			progress.progressProperty().bind(new AnimationProgressBinding(animationProperty));
			progress.setMaxWidth(1d/0d);
			progress.setMaxHeight(1d/0d);
		}
		
		Button playButton = new Button(); {
			final Shape playGraphic = new Polygon(0,0, 0,15, 15,7.5);
			final Shape pauseGraphic = new Polygon(0,0, 0,15, 6,15, 6,0, 9,0, 9,15, 15,15, 15,0);
			
			playButton.graphicProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty,
					playGraphic, pauseGraphic, playGraphic)
			);
			playButton.textProperty().bind(
				new AnimationPlayPauseBinding<>(
					  animationProperty, "Play", "Pause", "Resume")
			);
			playButton.onActionProperty().bind(
				new AnimationPlayPauseBinding<>(
					  animationProperty
					, playButtonEvent
					, (event) -> animationProperty.getValue().pause()
					, (event) -> animationProperty.getValue().play()
				)
			);
			playButton.setContentDisplay(ContentDisplay.LEFT);
			playButton.tooltipProperty().bind(
				new AnimationPlayPauseBinding<>(
					  animationProperty
					, new Tooltip("Play")
					, new Tooltip("Pause")
					, new Tooltip("Resume")
				)
			);
			playButton.setMaxWidth(1d/0d);
			playButton.setDefaultButton(true);
		}
		
		Button stopButton = new Button(); {
			final Shape stopGraphic = new Rectangle(10, 10);
			
			stopButton.setGraphic(stopGraphic);
			stopButton.setText("Stop");
			stopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			stopButton.setOnAction(x ->
				animationProperty.get().getOnFinished().handle(null)
			);
			stopButton.setTooltip(new Tooltip("Stop"));
			stopButton.disableProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, true, false, false)
			);
			stopButton.setMaxWidth(1d/0d);
			
		}
		
		Button snapshotButton = new Button(); {
			final SVGPath snapshotGraphic = new SVGPath();
			snapshotGraphic.setContent("M 3 0.5 L 2.25 2 L 0 2 L 0 8 L 2.0410156 8 A 3 3 0 0 1 2 7.5 A 3 3 0 0 1 5 4.5 A 3 3 0 0 1 8 7.5 A 3 3 0 0 1 7.953125 8 L 10 8 L 10 2 L 7.75 2 L 7 0.5 L 3 0.5 z M 1 3 L 2 3 L 2 4 L 1 4 L 1 3 z M 5 5.5 A 2 2 0 0 0 3 7.5 A 2 2 0 0 0 5 9.5 A 2 2 0 0 0 7 7.5 A 2 2 0 0 0 5 5.5 z ");
			
			snapshotButton.setGraphic(snapshotGraphic);
			snapshotButton.setText("Snapshot");
			snapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			snapshotButton.setOnAction(new SnapshotButtonActionHandler());
			snapshotButton.setTooltip(new Tooltip("Snapshot"));
			snapshotButton.setMaxWidth(1d/0d);
		}
		
		
		GridPane.setFillWidth(progress, true);
		
		this.node = new GridPane();
		this.node.add(playButton, 0, 0, GridPane.REMAINING, 1);
		this.node.add(progress, 0, 1, 2, 1);
		this.node.add(stopButton, 2, 1);
		this.node.add(snapshotButton, 3, 1);
		
		this.node.getColumnConstraints().addAll(
			  percentColumnConstraint(50)
			, percentColumnConstraint(40)
			, percentColumnConstraint(5)
			, percentColumnConstraint(5)
		);
	}
	
	public Node getNode() { return this.node; }
	
	/**
	 * A binding that displays represents the progress of an animation.
	 * <p>
	 * The output value is in the range [0.0-1.0]. The output value is 0.0 if the animation is {@code null}.
	 */
	private static final class AnimationProgressBinding extends DoubleBinding {
		private final ObjectProperty<Animation> animationProperty;
		private ObservableValue<Duration> currentCurrentTime;
		private ObservableValue<Duration> currentTotalTime;
		
		public AnimationProgressBinding(ObjectProperty<Animation> animationProperty) {
			this.animationProperty = animationProperty;
			updateCurrentTimeProps();
			animationProperty.addListener((binding, from, to) -> updateCurrentTimeProps());
		}
		
		@Override
		protected double computeValue() {
			if (currentCurrentTime == null) {
				return 0.0;
			} else {
				return currentCurrentTime.getValue().toMillis() /
					currentTotalTime.getValue().toMillis();
			}
		}
		
		private void updateCurrentTimeProps() {
			if (this.currentCurrentTime != null) {
				this.unbind(this.currentCurrentTime);
				this.unbind(this.currentTotalTime);
			}
			this.currentCurrentTime = null;
			this.currentTotalTime = null;
			if (this.animationProperty.get() != null) {
				this.currentCurrentTime = this.animationProperty.get().currentTimeProperty();
				this.currentTotalTime = this.animationProperty.get().totalDurationProperty();
				this.bind(this.currentCurrentTime);
				this.bind(this.currentTotalTime);
			}
			this.invalidate();
		}
	}
	
	/**
	 * A binding whose value depends on the statusProperty of an animation which
	 * is itself inside a Property.
	 * 
	 * This will return one of three values: the `startValue` if the animation is null,
	 * `pauseValue` if the animation is nonnull and currently running, and `resumeValue` otherwise.
	 */
	private static final class AnimationPlayPauseBinding<A> extends ObjectBinding<A> {
		private final ObjectProperty<Animation> animationProperty;
		private ObservableValue<Animation.Status> currentStatus;
		
		private final A startValue;
		private final A pauseValue;
		private final A resumeValue;
		
		public AnimationPlayPauseBinding(
			ObjectProperty<Animation> animationProperty,
			A startValue,
			A pauseValue,
			A resumeValue
		) {
			this.startValue = startValue;
			this.pauseValue = pauseValue;
			this.resumeValue = resumeValue;
			
			this.animationProperty = animationProperty;
			updateCurrentTimeProps();
			animationProperty.addListener((binding, from, to) -> updateCurrentTimeProps());
		}
		
		@Override
		protected A computeValue() {
			if (currentStatus == null) {
				return startValue;
			} else {
				if (currentStatus.getValue() == Animation.Status.RUNNING) {
					return pauseValue;
				} else {
					return resumeValue;
				}
			}
		}
		
		private void updateCurrentTimeProps() {
			if (this.currentStatus != null) {
				this.unbind(this.currentStatus);
			}
			this.currentStatus = null;
			if (this.animationProperty.get() != null) {
				this.currentStatus = this.animationProperty.get().statusProperty();
				this.bind(this.currentStatus);
			}
			this.invalidate();
		}
	}
	
	private final class SnapshotButtonActionHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			final double trimFromTop = ((Region) node.getParent()).getHeight();
			
			WritableImage snapshot = node.getScene().snapshot(null);
			WritableImage trimmedSnapshot = new WritableImage(
				snapshot.getPixelReader(),
				0,
				(int) trimFromTop,
				(int) snapshot.getWidth(),
				(int) (snapshot.getHeight() - trimFromTop)
			);
			
			// TRYTHIS: copy to BufferedImage and save to file with ImageIO instead?
			
			Stage snapshotWindow = new Stage(StageStyle.UTILITY);
			snapshotWindow.initOwner(node.getScene().getWindow());
			snapshotWindow.setScene(new Scene(new StackPane(new ImageView(trimmedSnapshot))));
			snapshotWindow.setTitle("Snapshot");
			snapshotWindow.show();
		}
	}
	
	/** Creates a ColumnConstraints representing a percent of an area */
	private final ColumnConstraints percentColumnConstraint(double percent) {
		ColumnConstraints retval = new ColumnConstraints();
		retval.setPercentWidth(percent);
		return retval;
	}
}
