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
package name.rayrobdod.fightStage.rasterizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import name.rayrobdod.fightStage.Animations;
import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.previewer.spi.NameSupplierPair;
import name.rayrobdod.fightStage.previewer.spi.SpellAnimationGroups;
import name.rayrobdod.fightStage.previewer.spi.UnitAnimationGroups;

/**
 * Converts an animation in the fxstage format to something in femp's format
 */
public final class Main extends Application {
	
	@Override
	public void start(Stage stage) {
		final List<NameSupplierPair<SpellAnimationGroup>> allSpells = SpellAnimationGroups.getAll();
		final List<NameSupplierPair<UnitAnimationGroup>> allUnits = UnitAnimationGroups.getAll();
		
		final String outputFileStr = this.getParameters().getNamed().get("d");
		final double framesPerSecond = Double.parseDouble(this.getParameters().getNamed().getOrDefault("fps", "30"));
		final Duration frameRate = Duration.seconds(1 / framesPerSecond);
		final int bitDepth = Integer.parseInt(this.getParameters().getNamed().getOrDefault("bitDepth", "-1"));
		final String spellAnim = getParameterValue(this.getParameters(), "spell");
		final boolean disableSmoothing = this.getParameters().getUnnamed().contains("--no-smooth");
		
		if (outputFileStr == null) {
			System.out.println("No output file: needs `--d=filename.png`");
			Platform.exit();
			
		} else if (null != spellAnim) {
			final List<NameSupplierPair<SpellAnimationGroup>> filteredSpells = allSpells.stream()
					.filter(x -> x.displayName.contains(spellAnim))
					.collect(Collectors.toList());
			
			if (0 == filteredSpells.size()) {
				System.out.println("No matches: " + spellAnim);
				Platform.exit();
			} else if (1 == filteredSpells.size()) {
				final SpellAnimationGroup spell = filteredSpells.get(0).supplier.get();
				
				final java.awt.Dimension canvasSize = new java.awt.Dimension(240, 180);
				final Translate objectCanvasOffset = new Translate(0, canvasSize.getHeight() * 2 / 3);
				final Scale scale = new Scale(0.5, 0.5);
				final Point2D target = new Point2D(2 * canvasSize.getWidth() / 3, 2 * canvasSize.getHeight() * -1 / 10);
				final Point2D origin = new Point2D(2 * canvasSize.getWidth() * 2 / 3, 2 * canvasSize.getHeight() * -1 / 10);
				
				spell.objectFrontLayer().getTransforms().addAll(objectCanvasOffset, scale);
				spell.objectBehindLayer().getTransforms().addAll(objectCanvasOffset, scale);
				spell.backgroundLayer().getTransforms().add(0, new Scale(canvasSize.getWidth(), canvasSize.getHeight()));
				if (disableSmoothing) {
					setSmoothRecursive(spell.objectFrontLayer(), false);
					setSmoothRecursive(spell.objectBehindLayer(), false);
					setSmoothRecursive(spell.backgroundLayer(), false);
				}
				final Node canvasBehind = new Group(forceThingsToStayInPlaceNode(canvasSize), spell.objectBehindLayer());
				final Node canvasFront = new Group(forceThingsToStayInPlaceNode(canvasSize), spell.objectFrontLayer());
				final Node canvasBackground = new Group(forceThingsToStayInPlaceNode(canvasSize), spell.backgroundLayer());
				final Animation anim = spell.getAnimation(origin, target, BattlePanAnimations.nil(), new MockShakeAnimationBiFunction(), Animations.nil());
				
				final Thread runner = new Thread(
					() -> {
						try {
							render(
								canvasBehind, anim,
								canvasSize, frameRate, bitDepth, outputFileStr + "_behind.png"
							);
							
							render(
								canvasFront, anim,
								canvasSize, frameRate, bitDepth, outputFileStr + "_front.png"
							);
							
							render(
								canvasBackground, anim,
								canvasSize, frameRate, bitDepth, outputFileStr + "_background.png"
							);
							
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						
						Platform.exit();
					},
					"Rasterizer"
				);
				runner.start();
			} else {
				System.out.println("Multiple matches found - be more specific: ");
				filteredSpells.forEach(x ->
					System.out.println("\t" + x.displayName)
				);
				Platform.exit();
			}
		// TODO else if unit
		} else {
			System.out.println("Choose a mode");
			Platform.exit();
		}
	}
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
	
	/**
	 * like `params.getNamed().get(key)`, except it returns `""` instead of `null` if `--key` is in the unnamed params list
	 */
	private static /* @Nullable */ String getParameterValue(Application.Parameters params, String key) {
		if (params.getNamed().get(key) != null) {
			return params.getNamed().get(key);
		} else {
			if (params.getUnnamed().contains("--" + key)) {
				return "";
			} else {
				return null;
			}
		}
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
	
	private static void setSmoothRecursive(Node n, boolean newValue) {
		if (n instanceof javafx.scene.Parent) {
			((javafx.scene.Parent) n).getChildrenUnmodifiable().forEach(child ->
				setSmoothRecursive(child, newValue)
			);
		} else if (n instanceof javafx.scene.SubScene) {
			setSmoothRecursive(((javafx.scene.SubScene) n).getRoot(), newValue);
		} else if (n instanceof javafx.scene.shape.Shape) {
			// There doesn't seem to be any way to turn off stroke anti-aliasing
			// The following only affects fills.
			((javafx.scene.shape.Shape) n).setSmooth(newValue);
		} else if (n instanceof javafx.scene.image.ImageView) {
			((javafx.scene.image.ImageView) n).setSmooth(newValue);
		}
	}
	
	/**
	 * An object with out-of-bounds rendering to make sure Node#render
	 * doesn't move visible items around arbitrarily
	 */
	private static Node forceThingsToStayInPlaceNode(java.awt.Dimension canvasSize) {
		final Rectangle forceThingsToStayInPlace = new Rectangle(
				-canvasSize.getWidth() / 2, -canvasSize.getHeight() / 2,
				canvasSize.getWidth() * 2, canvasSize.getHeight() * 2
		);
		forceThingsToStayInPlace.setFill(Color.TRANSPARENT);
		forceThingsToStayInPlace.setStroke(Color.BLACK);
		forceThingsToStayInPlace.setStrokeWidth(2);
		return forceThingsToStayInPlace;
	}
	
	/**
	 * @pre cannot be called on the main thread
	 */
	private static void render(
		Node canvas, Animation anim,
		java.awt.Dimension canvasSize, Duration frameRate, int bitDepth, String outputFileStr
	) throws InterruptedException, IOException {
		anim.setRate(0.001);
		anim.play();
		final int frameCount = (int) (anim.getTotalDuration().toMillis() / frameRate.toMillis());
		
		final Image frames[] = new Image[frameCount];
		// generate each frame
		for (int i = 0; i < frameCount; i++) {
			final int i2 = i;
			
			runLaterAndAwait(() -> {
				final Duration jumpToDur = frameRate.multiply(i2);
				anim.jumpTo(jumpToDur);
			});
			runLaterAndAwait(() -> {
				final SnapshotParameters parameters = new SnapshotParameters();
				parameters.setFill(Color.TRANSPARENT);
				parameters.setViewport(new Rectangle2D(0,0, canvasSize.getWidth(), canvasSize.getHeight()));
				final WritableImage snapshot = canvas.snapshot(parameters, null);
				frames[i2] = snapshot;
			});
		}
		
		// trim the frame's whitespace
		final java.awt.Rectangle trimmedBounds = new java.awt.Rectangle(80, 110, 0, 0);
		for (int k = 0; k < frameCount; k++)
		for (int i = 0; i < canvasSize.getWidth(); i++)
		for (int j = 0; j < canvasSize.getHeight(); j++) {
			if (0 != frames[k].getPixelReader().getArgb(i, j)) {
				trimmedBounds.add(i, j);
				trimmedBounds.add(i + 1, j + 1);
			}
		}
		
		if (trimmedBounds.width != 0) {
			// Compose frames
			final int columns = (int) Math.ceil(Math.sqrt( frames.length * trimmedBounds.height / trimmedBounds.width ));
			final int rows = (int) Math.ceil( ((float) frames.length) / columns );
			WritableImage sheet = new WritableImage(trimmedBounds.width * columns, trimmedBounds.height * rows);
			
			for (int i = 0; i < frameCount; i++) {
				final int xtile = i % columns;
				final int ytile = i / columns;
				final int x = xtile * trimmedBounds.width;
				final int y = ytile * trimmedBounds.height;
				
				sheet.getPixelWriter().setPixels(
					x, y,
					trimmedBounds.width, trimmedBounds.height,
					frames[i].getPixelReader(),
					trimmedBounds.x, trimmedBounds.y
				);
			}
			
			// Quantize image to fit in the specified bit depth
			if (bitDepth > 0) {
				final Set<Color> quantizePallette = QuantizePallette.apply(sheet, bitDepth);
				sheet = new WritableImage(
					new QuantizingPixelReader(sheet.getPixelReader(), QuantizePallette.apply(sheet, bitDepth)),
					// new TruncatingPixelReader(sheet.getPixelReader()),
					trimmedBounds.width * columns,
					trimmedBounds.height * rows
				);
			}
			
			// find hitFrame
			List<FindMockShakeAnimationStartTimesResult> shakeTimes = findMockShakeAnimationStartTimes(anim).collect(Collectors.toList());
			final String hitFramesStr = shakeTimes.stream().map(x -> (int) (x.startTime.toMillis() / frameRate.toMillis())).map(x -> "" + x).collect(Collectors.joining(", ", "[", "]"));
			final String shakeFramesStr = shakeTimes.stream().map(x -> (int) (x.duration.toMillis() / frameRate.toMillis()) * 2 / 3).map(x -> "" + x).collect(valueIfAllEqual()).orElse("TODO");
			final String shakeIntensityStr = shakeTimes.stream().map(x -> (int) (x.intensity / 2)).map(x -> "" + x).collect(valueIfAllEqual()).orElse("TODO");
			
			// Output file to disk
			final BufferedImage sheetSwing = SwingFXUtils.fromFXImage(sheet, null);
			final File outputFile = new File(outputFileStr);
			
			ImageIO.write(sheetSwing, "png", outputFile);
			System.out.println("{");
			System.out.println("\t\"name\": \"" + outputFile.getName() + "\",");
			System.out.println("\t\"path\": \"res/battle_anim/" + outputFile.getName() + "\",");
			System.out.println("\t\"frames\": " + frameCount + ",");
			System.out.println("\t\"width\": " + trimmedBounds.width + ",");
			System.out.println("\t\"height\": " + trimmedBounds.height + ",");
			System.out.println("\t\"columns\": " + columns + ",");
			System.out.println("\t\"offsetX\": " + (84 - trimmedBounds.x) + ",");
			System.out.println("\t\"offsetY\": " + (120 - trimmedBounds.y) + ",");
			System.out.println("\t\"speed\": " + frameRate.toSeconds() + ",");
			System.out.println("\t\"freeze\": -1" + ",");
			System.out.println("\t\"hitframes\": " + hitFramesStr + ",");
			System.out.println("\t\"shakeFrames\": " + shakeFramesStr + ",");
			System.out.println("\t\"shakeIntensity\": " + shakeIntensityStr + ",");
			System.out.println("\t\"soundMap\": TODO");
			System.out.println("}");
		} else {
			System.out.println("No image produced for " + outputFileStr);
		}
	}
	
	/* * * * * * * * Shake Animation Mocking * * * * * * * * */
	/**
	 * Finds the start time and values of any `MockShakeAnimation`s nested inside the haystack
	 */
	private static Stream<FindMockShakeAnimationStartTimesResult> findMockShakeAnimationStartTimes(Animation haystack) {
		if (haystack instanceof MockShakeAnimation) {
			return Stream.of(new FindMockShakeAnimationStartTimesResult((MockShakeAnimation) haystack));
		} else if (haystack instanceof SequentialTransition) {
			final List<FindMockShakeAnimationStartTimesResult> retval = new java.util.ArrayList<>();
			final List<Animation> childs = ((SequentialTransition) haystack).getChildren();
			Duration timeSoFar = Duration.ZERO;
			for (Animation child : childs) {
				List<FindMockShakeAnimationStartTimesResult> innerTimes = findMockShakeAnimationStartTimes(child).collect(Collectors.toList());
				for (FindMockShakeAnimationStartTimesResult innerTime : innerTimes) {
					retval.add(innerTime.plusStartTime(timeSoFar));
				}
				timeSoFar = timeSoFar.add(child.getTotalDuration());
			}
			return retval.stream();
		} else if (haystack instanceof ParallelTransition) {
			List<Animation> childs = ((ParallelTransition) haystack).getChildren();
			return childs.stream().flatMap(x -> findMockShakeAnimationStartTimes(x));
		} else {
			return Stream.empty();
		}
		
	}
	
	private static class FindMockShakeAnimationStartTimesResult {
		public final double intensity;
		public final Duration duration;
		public final Duration startTime;
		
		private FindMockShakeAnimationStartTimesResult(double intensity, Duration duration, Duration startTime) {
			this.intensity = intensity;
			this.duration = duration;
			this.startTime = startTime;
		}
		
		public FindMockShakeAnimationStartTimesResult(MockShakeAnimation anim) {
			this.intensity = anim.intensity;
			this.duration = anim.duration;
			this.startTime = Duration.ZERO;
		}
		
		public FindMockShakeAnimationStartTimesResult plusStartTime(Duration delta) {
			return new FindMockShakeAnimationStartTimesResult(this.intensity, this.duration, this.startTime.add(delta));
		}
	}
	
	private static class MockShakeAnimation extends javafx.animation.Transition {
		public double intensity;
		public Duration duration;
		protected void interpolate(double frac) {}
	}
	private static class MockShakeAnimationBiFunction implements ShakeAnimationBiFunction {
		// copied from the equally-private BattleAnimation.ShakeAnimationFactory
		private static final double DEFAULT_INTENSITY = 6;
		private static final Duration DEFAULT_DURATION = Duration.millis(160);
		
		public Animation apply() { return this.apply(DEFAULT_INTENSITY, DEFAULT_DURATION); }
		public Animation apply(double intensity) { return this.apply(intensity, DEFAULT_DURATION); }
		public Animation apply(Duration duration) { return this.apply(DEFAULT_INTENSITY, duration); }
		
		public Animation apply(double intensity, Duration duration) {
			MockShakeAnimation retval = new MockShakeAnimation();
			retval.intensity = intensity;
			retval.duration = duration;
			return retval;
		}
	}
	
	/* * * * * * * * Value if All Equal * * * * * * * * */
	private static class ValueIfAllEqualMid<E> {
		public static enum State { New, AllSame, Difference }
		
		public State state;
		public E value;
		
		public ValueIfAllEqualMid() {
			this.state = State.New;
			this.value = null;
		}
		
		public void add(E e) {
			switch (this.state) {
				case New : {
					this.state = State.AllSame;
					this.value = e;
					break;
				}
				case AllSame : {
					if (java.util.Objects.equals(this.value, e)) {
						// do nothing
					} else {
						this.state = State.Difference;
					}
					break;
				}
				case Difference : {
					break;
				}
			}
		}
		public ValueIfAllEqualMid<E> addAll(ValueIfAllEqualMid<E> rhs) {
			if (this.state == State.Difference) {
				return this;
			} else if (rhs.state == State.Difference) {
				return rhs;
			} else if (this.state == State.New) {
				return rhs;
			} else if (rhs.state == State.New) {
				return this;
			} else {// both are `AllSame`
				if (java.util.Objects.equals(this.value, rhs.value)) {
					return this;
				} else {
					ValueIfAllEqualMid<E> retval = new ValueIfAllEqualMid<>();
					retval.state = State.Difference;
					return retval;
				}
			}
		}
		public Optional<E> result() {
			return (this.state == State.AllSame ? Optional.of(value) : Optional.empty());
		}
	}
	
	/**
	 * A collector that, if all elements in a stream are equal, returns that value.
	 */
	private static final <E> Collector<E, ?, Optional<E>> valueIfAllEqual() {
		return Collector.<E, ValueIfAllEqualMid<E>, Optional<E>>of(
			ValueIfAllEqualMid::new,
			ValueIfAllEqualMid::add,
			ValueIfAllEqualMid::addAll,
			ValueIfAllEqualMid::result,
			Collector.Characteristics.UNORDERED
		);
	}
}
