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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A panel which control the playing of an animation
 */
final class MediaControlPanel {
	
	private final GridPane node;
	private final FileChooser filechooser;
	private final DirectoryChooser directorychooser;
	private final Parent snapContainer;
	
	/**
	 * @param animationProperty the animation that this's actions act upon
	 * @param playButtonEvent the action that is to occur upon
	 */
	public MediaControlPanel(
		final Function<ObjectProperty<Animation>, EventHandler<ActionEvent>> playButtonEventFunction
		, final Parent snapContainer
	) {
		this.snapContainer = snapContainer;
		
		final ObjectProperty<Animation> animationProperty = new SimpleObjectProperty<Animation>(null);
		final EventHandler<ActionEvent> playButtonEvent = playButtonEventFunction.apply(animationProperty);
		final ObjectProperty<Runnable> recordingTaskProperty = new SimpleObjectProperty<>(null);
		final BooleanProperty recordingTaskCanceledProperty = new SimpleBooleanProperty(false);
		
		final ProgressBar progress = new ProgressBar(0.0); {
			progress.progressProperty().bind(new AnimationProgressBinding(animationProperty));
			progress.setMaxWidth(1d/0d);
			progress.setMaxHeight(1d/0d);
		}
		
		final Button playButton = new Button(); {
			final Shape playGraphic = new Polygon(0,0, 0,15, 15,7.5);
			final Shape pauseGraphic = new Polygon(0,0, 0,15, 6,15, 6,0, 9,0, 9,15, 15,15, 15,0);
			
			playButton.getStyleClass().add("button-play");
			playButton.graphicProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty,
					playGraphic, pauseGraphic, playGraphic, playGraphic)
			);
			playButton.textProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty,
					  "Play", "Pause", "Resume", "Play")
			);
			playButton.onActionProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty
					, playButtonEvent
					, (event) -> animationProperty.getValue().pause()
					, (event) -> animationProperty.getValue().play()
					, (event) -> {}
				)
			);
			playButton.disableProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty,
					false, false, false, true)
			);
			playButton.setContentDisplay(ContentDisplay.LEFT);
			playButton.tooltipProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty
					, new Tooltip("Play")
					, new Tooltip("Pause")
					, new Tooltip("Resume")
					, new Tooltip("Play")
				)
			);
			playButton.setMaxWidth(1d/0d);
			playButton.setDefaultButton(true);
		}
		
		final Button stopButton = new Button(); {
			final Shape stopGraphic = new Rectangle(10, 10);
			
			stopButton.getStyleClass().add("button-stop");
			stopButton.setGraphic(stopGraphic);
			stopButton.setText("Stop");
			stopButton.setContentDisplay(ContentDisplay.LEFT);
			stopButton.onActionProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty
					, (event) -> {}
					, (event) -> java.util.Optional.ofNullable(animationProperty.get().getOnFinished()).ifPresent(x -> x.handle(null))
					, (event) -> java.util.Optional.ofNullable(animationProperty.get().getOnFinished()).ifPresent(x -> x.handle(null))
					, (event) -> recordingTaskCanceledProperty.set(true)
				)
			);
			stopButton.setTooltip(new Tooltip("Stop"));
			stopButton.disableProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty,
					true, false, false, false)
			);
			stopButton.setMaxWidth(1d/0d);
			
		}
		
		final Button snapshotButton = new Button(); {
			final SVGPath snapshotGraphic = new SVGPath();
			snapshotGraphic.setContent("M 3 0.5 L 2.25 2 L 0 2 L 0 8 L 2.0410156 8 A 3 3 0 0 1 2 7.5 A 3 3 0 0 1 5 4.5 A 3 3 0 0 1 8 7.5 A 3 3 0 0 1 7.953125 8 L 10 8 L 10 2 L 7.75 2 L 7 0.5 L 3 0.5 z M 1 3 L 2 3 L 2 4 L 1 4 L 1 3 z M 5 5.5 A 2 2 0 0 0 3 7.5 A 2 2 0 0 0 5 9.5 A 2 2 0 0 0 7 7.5 A 2 2 0 0 0 5 5.5 z ");
			
			snapshotButton.getStyleClass().add("button-shapshot");
			snapshotButton.setGraphic(snapshotGraphic);
			snapshotButton.setText("Snapshot");
			snapshotButton.setContentDisplay(ContentDisplay.LEFT);
			snapshotButton.setOnAction(new SnapshotButtonActionHandler());
			snapshotButton.setTooltip(new Tooltip("Snapshot"));
			snapshotButton.disableProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty,
					true, false, false, true)
			);
			snapshotButton.setMaxWidth(1d/0d);
		}
		
		final Button recordButton = new Button(); {
			final Shape recordGraphic = new Circle(7.5);
			
			recordButton.getStyleClass().add("button-record");
			recordButton.setGraphic(recordGraphic);
			recordButton.setText("Record");
			recordButton.setContentDisplay(ContentDisplay.LEFT);
			recordButton.setOnAction(new RecordButtonActionHandler(animationProperty, playButtonEvent, recordingTaskProperty, recordingTaskCanceledProperty));
			recordButton.setTooltip(new Tooltip("Record"));
			recordButton.disableProperty().bind(
				new AnimationPlayPauseBinding<>(animationProperty, recordingTaskProperty,
					false, true, true, true)
			);
			recordButton.setMaxWidth(1d/0d);
		}
		
		GridPane.setFillWidth(progress, true);
		
		this.node = new GridPane();
		this.node.add(progress, 0, 0, GridPane.REMAINING, 1);
		this.node.add(playButton, 0, 1);
		this.node.add(recordButton, 1, 1);
		this.node.add(stopButton, 2, 1);
		this.node.add(snapshotButton, 3, 1);
		
		this.node.getColumnConstraints().addAll(
			  new ColumnConstraints(0.0, 480 / 3, 1d/0d, Priority.ALWAYS, HPos.CENTER, true)
			, new ColumnConstraints(0.0, 480 / 3, 1d/0d, Priority.ALWAYS, HPos.CENTER, true)
			, new ColumnConstraints(0.0, 480 / 6, 1d/0d, Priority.ALWAYS, HPos.CENTER, true)
			, new ColumnConstraints(0.0, 480 / 6, 1d/0d, Priority.ALWAYS, HPos.CENTER, true)
		);
		
		this.filechooser = new FileChooser();
		final java.util.Iterator<ImageWriterSpi> spis = IIORegistry.getDefaultInstance().getServiceProviders(ImageWriterSpi.class, true);
		while (spis.hasNext()) {
			ImageWriterSpi spi = spis.next();
			String name = spi.getFormatNames()[0];
			String[] suffixes = spi.getFileSuffixes();
			filechooser.getExtensionFilters().add(new ExtensionFilter(name, suffixes));
		}
		filechooser.getExtensionFilters().sort(java.util.Comparator.comparing(x -> x.getExtensions().get(0), MediaControlPanel::compareStringWithPngFirst));
		filechooser.setInitialFileName("snaphot");
		
		this.directorychooser = new DirectoryChooser();
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
	 * This will return one of three values: the `idleValue` if the animation is null,
	 * `playingValue` if the animation is nonnull and currently running, and `pausedValue` otherwise.
	 */
	private static final class AnimationPlayPauseBinding<A> extends ObjectBinding<A> {
		private final ObjectProperty<Animation> animationProperty;
		private ObservableValue<Animation.Status> currentStatus;
		private final ObjectProperty<Runnable> recordingTaskProperty;
		
		private final A idleValue;
		private final A playingValue;
		private final A pausedValue;
		private final A recordingValue;
		
		public AnimationPlayPauseBinding(
			ObjectProperty<Animation> animationProperty,
			ObjectProperty<Runnable> recordingTaskProperty,
			A idleValue,
			A playingValue,
			A pausedValue,
			A recordingValue
		) {
			this.idleValue = idleValue;
			this.playingValue = playingValue;
			this.pausedValue = pausedValue;
			this.recordingValue = recordingValue;
			
			this.animationProperty = animationProperty;
			updateCurrentStatus();
			animationProperty.addListener((binding, from, to) -> updateCurrentStatus());
			
			this.bind(recordingTaskProperty);
			this.recordingTaskProperty = recordingTaskProperty;
		}
		
		@Override
		protected A computeValue() {
			if (currentStatus == null) {
				return idleValue;
			} else {
				if (currentStatus.getValue() == Animation.Status.RUNNING) {
					if (recordingTaskProperty.getValue() == null) {
						return playingValue;
					} else {
						return recordingValue;
					}
				} else {
					return pausedValue;
				}
			}
		}
		
		private void updateCurrentStatus() {
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
			final Node snapNode = findSnapNode();
			
			final SnapshotParameters parameters = new SnapshotParameters();
			parameters.setFill(Color.TRANSPARENT);
			final WritableImage snapshot = snapNode.snapshot(parameters, null);
			final BufferedImage snapshotSwing = SwingFXUtils.fromFXImage(snapshot, null);
			final File snapshotFile = filechooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
			
			if (snapshotFile != null) {
				try {
					ImageIO.write(snapshotSwing, filechooser.getSelectedExtensionFilter().getDescription(), snapshotFile);
				} catch (IOException ex) {
					final Alert errorWindow = new Alert(Alert.AlertType.ERROR, ex.getMessage(), javafx.scene.control.ButtonType.OK);
					errorWindow.initOwner(node.getScene().getWindow());
					errorWindow.setTitle(((Stage) node.getScene().getWindow()).getTitle());
					errorWindow.setHeaderText("Could not save snapshot");
					errorWindow.showAndWait();
				}
			}
		}
	}
	
	private final class RecordButtonActionHandler implements EventHandler<ActionEvent> {
		private final ObjectProperty<Animation> animationProperty;
		private final EventHandler<ActionEvent> playButtonEvent;
		private final ObjectProperty<Runnable> recordingTaskProperty;
		private final BooleanProperty recordingTaskCanceledProperty;
		
		public RecordButtonActionHandler(
			  final ObjectProperty<Animation> animationProperty
			, final EventHandler<ActionEvent> playButtonEvent
			, final ObjectProperty<Runnable> recordingTaskProperty
			, final BooleanProperty recordingTaskCanceledProperty
		) {
			this.animationProperty = animationProperty;
			this.playButtonEvent = playButtonEvent;
			this.recordingTaskProperty = recordingTaskProperty;
			this.recordingTaskCanceledProperty = recordingTaskCanceledProperty;
		}
		
		public void handle(ActionEvent event) {
			recordingTaskCanceledProperty.set(false);
			final File recordDir = directorychooser.showDialog(((Node) event.getSource()).getScene().getWindow());
			if (recordDir != null) {
				if (recordDir.isDirectory() && recordDir.list().length != 0) {
					final Alert errorWindow = new Alert(Alert.AlertType.ERROR, "The selected directory is not empty. Halting recording.", javafx.scene.control.ButtonType.OK);
					errorWindow.initOwner(node.getScene().getWindow());
					errorWindow.setTitle(((Stage) node.getScene().getWindow()).getTitle());
					errorWindow.setHeaderText("Could not record");
					errorWindow.showAndWait();
				} else {
					playButtonEvent.handle(null);
					// playButtonEvent.handle has side effects that result in snapNode
					// existing and animationProperty becoming non-null
					animationProperty.get().setRate(0.0);
					final Node snapNode = findSnapNode();
					
					final Duration animDur = animationProperty.get().getTotalDuration();
					final Duration frameRate = Duration.seconds(1d / 30d);
					final int frames = (int) (animDur.toMillis() / frameRate.toMillis());
					
					final Exception[] imageioWriteException = new Exception[1];
					
					// javafx.concurrent.Task is interesting, but Task#cancel does nothing
					// and I don't use any other feature
					recordingTaskProperty.setValue(new Runnable() {
						@Override public void run() {
							try {
								// Obtain snapNode's size
									// by letting the node render at an arbitrary time
									// use that size in the SnapshotParameters to ensure that every frame has the same dimensions
									// the 1-pixel trim in the viewport is to exclude a one-pixel transparent border that seems to be included otherwise
								try {
									runLaterAndAwait(() -> {
										animationProperty.get().jumpTo(Duration.ZERO);
									});
								} catch (InterruptedException ex) {
									imageioWriteException[0] = ex;
								}
								final javafx.geometry.Bounds bounds1 = snapNode.getBoundsInParent();
								final javafx.geometry.Rectangle2D bounds2 = new javafx.geometry.Rectangle2D(bounds1.getMinX() + 1, bounds1.getMinY() + 1, bounds1.getWidth() - 2, bounds1.getHeight() - 2);
								
								
								// Render each frame and write it to disk
								for (int i = 0; i < frames; i++) {
									final int i2 = i;
									if (recordingTaskCanceledProperty.get()) {
										break;
									}
									if (imageioWriteException[0] != null) {
										break;
									}
									
									try {
										runLaterAndAwait(() -> {
											final Duration jumpToDur = frameRate.multiply(i2);
											animationProperty.get().jumpTo(jumpToDur);
										});
									} catch (InterruptedException ex) {
										imageioWriteException[0] = ex;
										break;
									}
									
									try {
										runLaterAndAwait(() -> {
											try {
												final SnapshotParameters parameters = new SnapshotParameters();
												parameters.setFill(Color.TRANSPARENT);
												parameters.setViewport(bounds2);
												final WritableImage snapshot = snapNode.snapshot(parameters, null);
												final BufferedImage snapshotSwing = SwingFXUtils.fromFXImage(snapshot, null);
												final File snapshotFile = new File(recordDir, String.format("%04d", i2) + ".png");
												
												ImageIO.write(snapshotSwing, "png", snapshotFile);
											} catch (IOException ex) {
												imageioWriteException[0] = ex;
											}
										});
									} catch (InterruptedException ex) {
										imageioWriteException[0] = ex;
										break;
									}
								}
							} finally {
								Platform.runLater(() -> {
									animationProperty.get().setRate(1);
									java.util.Optional.ofNullable(animationProperty.get().getOnFinished()).ifPresent(x -> x.handle(null));
									recordingTaskProperty.setValue(null);
								});
							}
						}
					});
					
					final Thread t = new Thread(recordingTaskProperty.get(), "FightStageRecorder");
					t.setDaemon(false);
					t.start();
					
					if (imageioWriteException[0] != null) {
						final Alert errorWindow = new Alert(Alert.AlertType.ERROR, imageioWriteException[0].getMessage(), javafx.scene.control.ButtonType.OK);
						errorWindow.initOwner(node.getScene().getWindow());
						errorWindow.setTitle(((Stage) node.getScene().getWindow()).getTitle());
						errorWindow.setHeaderText("Could not record");
						errorWindow.showAndWait();
					}
				}
			}
		}
	}
	
	private Node findSnapNode() {
		final List<Node> gamePaneChilds = snapContainer.getChildrenUnmodifiable();
		final Node animNode = (gamePaneChilds.size() >= 1 ? gamePaneChilds.get(gamePaneChilds.size() - 1) : new Text("placeholder"));
		
		return animNode;
	}
	
	/**
	 * Runs the given action on the JavaFX thread, and waits for it to be called
	 * and any subsequent rendering events to occur before returning control to the caller.
	 *
	 * MUST NOT be called on the JavaFX application thread
	 */
	private static void runLaterAndAwait(Runnable action) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {
			action.run();
			Platform.runLater(() -> latch.countDown());
		});
		latch.await();
	}
	
	/** The default string sort, except that "png" compares less than all other strings */
	private static int compareStringWithPngFirst(String a, String b) {
		return (a.equals(b) ? 0 : ("png".equals(a) ? -1 : ("png".equals(b) ? 1 : a.compareTo(b))));
	}
	
	/** Creates a ColumnConstraints representing a percent of an area */
	private final ColumnConstraints percentColumnConstraint(double percent) {
		ColumnConstraints retval = new ColumnConstraints();
		retval.setPercentWidth(percent);
		return retval;
	}
}
