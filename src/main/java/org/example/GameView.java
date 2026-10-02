package org.example;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GameView {

    private final Stage stage;

    private Canvas gameCanvas;
    private Label playerLabel;
    private Label levelLabel;
    private Label scoreLabel;
    private Label linesLabel;
    private Label musicLabel;
    private Label soundLabel;

    public GameView(Stage stage) {
        this.stage = stage;
    }

    public void showSplash(Runnable nextScreen) {

        Label title = new Label("TETRIS");

        title.setStyle(
                "-fx-font-size: 48px; " +
                        "-fx-font-weight: bold;"
        );

        Label course = new Label(
                "2006ICT Object Oriented Software Development"
        );

        Label group = new Label("Group: PG12");

        Label members = new Label(
                "Weihao Lin | Jake Rosman | Samuel Glancy"
        );

        VBox root = new VBox(
                15,
                title,
                course,
                group,
                members
        );

        root.setAlignment(Pos.CENTER);

        Scene scene =
                new Scene(root, 700, 450);

        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();

        PauseTransition delay =
                new PauseTransition(
                        Duration.seconds(3)
                );

        delay.setOnFinished(
                event -> nextScreen.run()
        );

        delay.play();
    }

    public void showMainMenu(
            Runnable playAction,
            Runnable configurationAction,
            Runnable highScoresAction,
            Runnable exitAction
    ) {

        Label title = new Label("TETRIS");

        title.setStyle(
                "-fx-font-size: 42px; " +
                        "-fx-font-weight: bold;"
        );

        Button playButton =
                new Button("Play");

        Button configurationButton =
                new Button("Configuration");

        Button highScoresButton =
                new Button("High Scores");

        Button exitButton =
                new Button("Exit");

        playButton.setPrefWidth(200);
        configurationButton.setPrefWidth(200);
        highScoresButton.setPrefWidth(200);
        exitButton.setPrefWidth(200);

        playButton.setOnAction(
                event -> playAction.run()
        );

        configurationButton.setOnAction(
                event -> configurationAction.run()
        );

        highScoresButton.setOnAction(
                event -> highScoresAction.run()
        );

        exitButton.setOnAction(
                event -> exitAction.run()
        );

        VBox root = new VBox(
                20,
                title,
                playButton,
                configurationButton,
                highScoresButton,
                exitButton
        );

        root.setAlignment(Pos.CENTER);

        Scene scene =
                new Scene(root, 700, 450);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public void showConfiguration(
            GameConfig config,
            Consumer<GameConfig> saveAction
    ) {

        Label title =
                new Label("Configuration");

        title.setStyle(
                "-fx-font-size: 30px; " +
                        "-fx-font-weight: bold;"
        );

        ComboBox<String> fieldSize =
                new ComboBox<>();

        fieldSize.getItems().addAll(
                "10 x 20",
                "12 x 24",
                "15 x 30"
        );

        fieldSize.setValue(
                config.fieldWidth() +
                        " x " +
                        config.fieldHeight()
        );

        Slider levelSlider =
                new Slider(
                        1,
                        10,
                        config.level()
                );

        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        levelSlider.setBlockIncrement(1);
        levelSlider.setSnapToTicks(true);
        levelSlider.setPrefWidth(300);

        Label levelValue =
                new Label(
                        "Level: " +
                                config.level()
                );

        levelSlider.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            int level =
                                    (int) Math.round(
                                            newValue.doubleValue()
                                    );

                            levelValue.setText(
                                    "Level: " + level
                            );
                        }
                );

        CheckBox music =
                new CheckBox("Music");

        CheckBox sound =
                new CheckBox("Sound Effects");

        CheckBox aiPlay =
                new CheckBox("AI Play");

        CheckBox extendedMode =
                new CheckBox("Extended Mode");

        music.setSelected(
                config.musicEnabled()
        );

        sound.setSelected(
                config.soundEnabled()
        );

        aiPlay.setSelected(
                config.aiPlayEnabled()
        );

        extendedMode.setSelected(
                config.extendedModeEnabled()
        );

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(150);

        backButton.setOnAction(event -> {

            String[] fieldValues =
                    fieldSize.getValue()
                            .split(" x ");

            GameConfig updatedConfig =
                    new GameConfig(
                            Integer.parseInt(
                                    fieldValues[0]
                            ),
                            Integer.parseInt(
                                    fieldValues[1]
                            ),
                            (int) Math.round(
                                    levelSlider.getValue()
                            ),
                            music.isSelected(),
                            sound.isSelected(),
                            aiPlay.isSelected(),
                            extendedMode.isSelected()
                    );

            saveAction.accept(updatedConfig);
        });

        VBox root = new VBox(
                15,
                title,
                new Label("Field Size"),
                fieldSize,
                levelValue,
                levelSlider,
                music,
                sound,
                aiPlay,
                extendedMode,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 700, 500);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public void showHighScores(
            List<HighScore> highScores,
            Runnable clearAction,
            Runnable backAction
    ) {

        Label title =
                new Label("High Scores");

        title.setStyle(
                "-fx-font-size: 30px; " +
                        "-fx-font-weight: bold;"
        );

        VBox scores =
                new VBox(8);

        scores.setAlignment(Pos.CENTER);

        if (highScores.isEmpty()) {
            scores.getChildren().add(
                    new Label("No high scores yet")
            );
        } else {

            int position = 1;

            for (HighScore highScore : highScores) {

                scores.getChildren().add(
                        new Label(
                                position +
                                        ". " +
                                        highScore.name() +
                                        " - " +
                                        highScore.score()
                        )
                );

                position++;
            }
        }

        Button clearButton =
                new Button("Clear Scores");

        Button backButton =
                new Button("Back");

        clearButton.setPrefWidth(150);
        backButton.setPrefWidth(150);

        clearButton.setOnAction(
                event -> clearAction.run()
        );

        backButton.setOnAction(
                event -> backAction.run()
        );

        VBox root = new VBox(
                20,
                title,
                scores,
                clearButton,
                backButton
        );

        root.setAlignment(Pos.CENTER);

        Scene scene =
                new Scene(root, 700, 500);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public Scene showGame(
            GameModel model,
            Runnable backAction,
            boolean musicEnabled,
            boolean soundEnabled
    ) {

        gameCanvas =
                new Canvas(
                        model.getCols() *
                                model.getBlockSize(),
                        model.getRows() *
                                model.getBlockSize()
                );

        playerLabel =
                new Label("Player: Human");

        levelLabel =
                new Label(
                        "Level: " +
                                model.getLevel()
                );

        scoreLabel =
                new Label("Score: 0");

        linesLabel =
                new Label("Lines: 0");

        musicLabel =
                new Label(
                        "Music: " +
                                (musicEnabled ? "On" : "Off")
                );

        soundLabel =
                new Label(
                        "Sound: " +
                                (soundEnabled ? "On" : "Off")
                );

        HBox gameDetails =
                new HBox(
                        20,
                        playerLabel,
                        levelLabel,
                        scoreLabel,
                        linesLabel,
                        musicLabel,
                        soundLabel
                );

        gameDetails.setAlignment(Pos.CENTER);

        Label controls = new Label(
                "← → Move   ↑ Rotate   ↓ Down   P Pause   M Music   S Sound"
        );

        Button backButton =
                new Button("Back to Menu");

        backButton.setOnAction(
                event -> backAction.run()
        );

        VBox root = new VBox(
                10,
                gameDetails,
                gameCanvas,
                controls,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        double sceneWidth =
                Math.max(
                        500,
                        gameCanvas.getWidth() + 80
                );

        double sceneHeight =
                gameCanvas.getHeight() + 135;

        Scene scene =
                new Scene(
                        root,
                        sceneWidth,
                        sceneHeight
                );

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();

        drawGame(model);

        root.requestFocus();

        return scene;
    }

    public void drawGame(GameModel model) {

        if (gameCanvas == null) {
            return;
        }

        scoreLabel.setText(
                "Score: " +
                        model.getScore()
        );

        linesLabel.setText(
                "Lines: " +
                        model.getLinesErased()
        );

        levelLabel.setText(
                "Level: " +
                        model.getLevel()
        );

        GraphicsContext gc =
                gameCanvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);

        gc.fillRect(
                0,
                0,
                gameCanvas.getWidth(),
                gameCanvas.getHeight()
        );

        int[][] board =
                model.getBoard();

        int blockSize =
                model.getBlockSize();

        for (int row = 0;
             row < model.getRows();
             row++) {

            for (int col = 0;
                 col < model.getCols();
                 col++) {

                double x =
                        col * blockSize;

                double y =
                        row * blockSize;

                if (board[row][col] != 0) {

                    gc.setFill(
                            model.getPieceColor(
                                    board[row][col]
                            )
                    );

                    gc.fillRect(
                            x + 1,
                            y + 1,
                            blockSize - 2,
                            blockSize - 2
                    );
                }

                gc.setStroke(
                        Color.rgb(
                                70,
                                70,
                                70
                        )
                );

                gc.strokeRect(
                        x,
                        y,
                        blockSize,
                        blockSize
                );
            }
        }

        Tetromino currentTetromino =
                model.getCurrentTetromino();

        if (
                currentTetromino != null &&
                        !model.isGameOver()
        ) {

            currentTetromino.setSmoothOffset(
                    model.getSmoothOffset()
            );

            currentTetromino.draw(gc);
        }

        if (model.isPaused()) {
            drawOverlay(
                    gc,
                    "PAUSED",
                    Color.WHITE
            );
        }

        if (model.isGameOver()) {
            drawOverlay(
                    gc,
                    "GAME OVER",
                    Color.RED
            );
        }
    }

    private void drawOverlay(
            GraphicsContext gc,
            String text,
            Color color
    ) {

        gc.setFill(
                Color.rgb(
                        0,
                        0,
                        0,
                        0.72
                )
        );

        gc.fillRect(
                0,
                0,
                gameCanvas.getWidth(),
                gameCanvas.getHeight()
        );

        gc.setFill(color);
        gc.setFont(Font.font(30));

        double textX =
                Math.max(
                        20,
                        gameCanvas.getWidth() / 2 - 90
                );

        double textY =
                gameCanvas.getHeight() / 2;

        gc.fillText(
                text,
                textX,
                textY
        );
    }

    public Optional<String> requestPlayerName(
            int score
    ) {

        TextInputDialog dialog =
                new TextInputDialog("Player");

        dialog.setTitle("New High Score");
        dialog.setHeaderText(
                "Score: " + score
        );
        dialog.setContentText(
                "Enter your name:"
        );

        return dialog.showAndWait();
    }

    public boolean confirmClearScores() {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle("Clear High Scores");
        alert.setHeaderText(
                "Clear all high scores?"
        );

        return alert.showAndWait()
                .orElse(ButtonType.CANCEL) ==
                ButtonType.OK;
    }

    public boolean confirmExit() {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle("Exit");
        alert.setHeaderText(
                "Exit Tetris?"
        );
        alert.setContentText(
                "Are you sure you want to exit?"
        );

        ButtonType yesButton =
                new ButtonType("Yes");

        ButtonType noButton =
                new ButtonType("No");

        alert.getButtonTypes().setAll(
                yesButton,
                noButton
        );

        return alert.showAndWait()
                .orElse(noButton) ==
                yesButton;
    }

    public void close() {
        stage.close();
    }
    public void updateAudioStatus(
            boolean musicEnabled,
            boolean soundEnabled
    ) {

        if (musicLabel != null) {
            musicLabel.setText(
                    "Music: " +
                            (musicEnabled ? "On" : "Off")
            );
        }

        if (soundLabel != null) {
            soundLabel.setText(
                    "Sound: " +
                            (soundEnabled ? "On" : "Off")
            );
        }
    }

}
