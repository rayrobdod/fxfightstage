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
import java.util.stream.Collectors;

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
				final Translate canvasOffset = new Translate(0, canvasSize.getHeight() * 2 / 3);
				final Scale scale = new Scale(0.5, 0.5);
				final Point2D target = new Point2D(2 * canvasSize.getWidth() / 3, 2 * canvasSize.getHeight() * -1 / 10);
				final Point2D origin = new Point2D(2 * canvasSize.getWidth() * 2 / 3, 2 * canvasSize.getHeight() * -1 / 10);
				
				final Rectangle forceThingsToStayInPlace = new Rectangle(- canvasSize.getWidth() / 2, - canvasSize.getHeight() / 2, canvasSize.getWidth() * 2, canvasSize.getHeight() * 2);
				forceThingsToStayInPlace.setFill(Color.TRANSPARENT);
				forceThingsToStayInPlace.setStroke(Color.BLACK);
				forceThingsToStayInPlace.setStrokeWidth(2);
				spell.getForeground().getTransforms().addAll(canvasOffset, scale);
				spell.getBackground().getTransforms().addAll(canvasOffset, scale);
				final Node canvas = new Group(forceThingsToStayInPlace, spell.getBackground(), spell.getForeground());
				final Animation shakeAnim = new javafx.animation.FillTransition(Duration.ZERO, new Rectangle(), Color.RED, Color.GREEN);
				final Animation anim = spell.getAnimation(origin, target, Animations.nil(), shakeAnim);
				
				anim.setRate(0.001);
				anim.play();
				final int frameCount = (int) (anim.getTotalDuration().toMillis() / frameRate.toMillis());
				
				final Thread runner = new Thread(
					() -> {
						final Image frames[] = new Image[frameCount];
						
						try {
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
								}
							}
							
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
							
							// Quantize image to fit in 256 colors
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
							Optional<Duration> shakeTime = findInnerStartTime(anim, shakeAnim);
							String shakeTimeStr = shakeTime.map(x -> (int) (x.toMillis() / frameRate.toMillis())).map(x -> "[" + x.toString() + "]").orElse("TODO");
							
							
							// Ouptut file to disk
							final BufferedImage sheetSwing = SwingFXUtils.fromFXImage(sheet, null);
							final File outputFile = new File(outputFileStr);
							
							ImageIO.write(sheetSwing, "png", outputFile);
							System.out.println("{");
							System.out.println("\t\"name\": TODO");
							System.out.println("\t\"path\": TODO");
							System.out.println("\t\"frames\": " + frameCount + ",");
							System.out.println("\t\"width\": " + trimmedBounds.width + ",");
							System.out.println("\t\"height\": " + trimmedBounds.height + ",");
							System.out.println("\t\"columns\": " + columns + ",");
							System.out.println("\t\"offsetX\": " + (84 - trimmedBounds.x) + ",");
							System.out.println("\t\"offsetY\": " + (120 - trimmedBounds.y) + ",");
							System.out.println("\t\"speed\": " + frameRate.toSeconds() + ",");
							System.out.println("\t\"freeze\": -1" + ",");
							System.out.println("\t\"hitframes\": " + shakeTimeStr + ",");
							System.out.println("\t\"shakeFrames\": TODO");
							System.out.println("\t\"shakeIntensity\": TODO");
							System.out.println("\t\"soundMap\": TODO");
							System.out.println("}");
							
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
	
	/** @pre assumes at most one needle per haystack */
	private static Optional<Duration> findInnerStartTime(Animation haystack, Animation needle) {
		if (haystack == needle) {
			return Optional.of(Duration.ZERO);
		} else if (haystack instanceof SequentialTransition) {
			final List<Animation> childs = ((SequentialTransition) haystack).getChildren();
			Duration timeSoFar = Duration.ZERO;
			for (Animation child : childs) {
				Optional<Duration> innerTime = findInnerStartTime(child, needle);
				if (innerTime.isPresent()) {
					final Duration timeSoFar2 = timeSoFar;
					return innerTime.map(x -> x.add(timeSoFar2));
				} else {
					timeSoFar = timeSoFar.add(child.getTotalDuration());
				}
			}
			return Optional.empty();
		} else if (haystack instanceof ParallelTransition) {
			List<Animation> childs = ((ParallelTransition) haystack).getChildren();
			return childs.stream().flatMap(x -> findInnerStartTime(x, needle).stream()).findFirst();
		} else {
			return Optional.empty();
		}
		
	}
}
