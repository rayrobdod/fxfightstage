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
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public final class Main extends Application {
	
	private static final Dimension2D gamePaneSize = new Dimension2D(480, 320);
	
	@Override
	public void start(Stage stage) {
		
		final StackPane gamePane = new StackPane();
		gamePane.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(Color.BLACK, null, null)));
		gamePane.setPrefWidth(gamePaneSize.getWidth());
		gamePane.setPrefHeight(gamePaneSize.getHeight());
		
		final SettingsPanel settings = new SettingsPanel(gamePane);
		
		
		final BorderPane mainPane = new BorderPane();
		mainPane.setTop(settings.getNode());
		mainPane.setCenter(gamePane);
		
		final Scene mainScene = new Scene(mainPane);
		
		stage.setTitle("Battle Animation Demo");
		stage.setScene(mainScene);
		stage.show();
	}
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
}
