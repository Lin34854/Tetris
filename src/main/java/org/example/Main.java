package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        GameModel model =
                new GameModel();

        GameView view =
                new GameView(stage);

        ConfigManager configManager =
                new ConfigManager();

        HighScoreManager highScoreManager =
                new HighScoreManager();

        GameController controller =
                new GameController(
                        model,
                        view,
                        configManager,
                        highScoreManager
                );

        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
