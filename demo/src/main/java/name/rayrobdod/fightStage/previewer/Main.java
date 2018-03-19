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

import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * An application that allows a user to set variables related to a
 * {@link name.rayrobdod.fightStage.BattleAnimation} and preview
 * that BattleAnimation
 */
public final class Main extends Application {
	
	private static final Dimension2D gamePaneSize = new Dimension2D(480, 320);
	
	@Override
	public void start(Stage stage) {
		
		final StackPane gamePane = new StackPane();
		gamePane.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.BLACK, null, null)));
		gamePane.setPrefWidth(gamePaneSize.getWidth());
		gamePane.setPrefHeight(gamePaneSize.getHeight());
		
		final SettingsPanel settings = new SettingsPanel();
		final MediaControlPanel mediaControl = new MediaControlPanel(
			settings.animationSettings.apply(gamePane)
			, gamePane
		);
		
		final ScrollPane settingScroll = new ScrollPane(settings.getNode());
		settingScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		settingScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		settingScroll.setFitToWidth(true);
		
		final VBox controlPanel = new VBox(
			settingScroll,
			mediaControl.getNode()
		);
		controlPanel.setMaxHeight(Region.USE_PREF_SIZE);
		
		final SplitPane mainPane = new SplitPane(
			controlPanel,
			gamePane
		);
		mainPane.setOrientation(Orientation.VERTICAL);
		
		final Scene mainScene = new Scene(mainPane);
		
		stage.setTitle("Battle Animation Demo");
		stage.setScene(mainScene);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
}
