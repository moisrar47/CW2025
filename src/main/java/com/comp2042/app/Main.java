package com.comp2042.app;

import com.comp2042.model.HighScoreManager;
import com.comp2042.controller.GameController;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private static final String GAME_LAYOUT_FXML = "gameLayout.fxml";
    private static final String WINDOW_TITLE = "TetrisJFX";
    private static final int SCENE_WIDTH = 480;
    private static final int SCENE_HEIGHT = 640;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL layoutUrl = getClass().getClassLoader().getResource(GAME_LAYOUT_FXML);
        if (layoutUrl == null) {
            throw new IllegalStateException("Cannot find " + GAME_LAYOUT_FXML + " on the classpath");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(layoutUrl);
        Parent root = fxmlLoader.load();
        GuiController guiController = fxmlLoader.getController();

        primaryStage.setTitle(WINDOW_TITLE);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);

        // lock the window size and centre it
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        primaryStage.show();

        // create and load high scores
        HighScoreManager highScoreManager = new HighScoreManager("highscores.txt");
        new GameController(guiController, highScoreManager);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
