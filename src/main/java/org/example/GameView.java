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
    private Canvas secondGameCanvas;
    private Canvas player1NextCanvas;
    private Canvas player2NextCanvas;

    private Label player1LevelLabel;
    private Label player1ScoreLabel;
    private Label player1LinesLabel;

    private Label player2LevelLabel;
    private Label player2ScoreLabel;
    private Label player2LinesLabel;

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

        CheckBox twoPlayer =
                new CheckBox("Two Player");

        music.setSelected(
                config.musicEnabled()
        );

        sound.setSelected(
                config.soundEnabled()
        );

        aiPlay.setSelected(
                config.aiPlayEnabled()
        );

        twoPlayer.setSelected(
                config.twoPlayerEnabled()
        );

        Label modeHelp = new Label(
                "AI Play adds an AI opponent. Two Player adds a second human player."
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
                            twoPlayer.isSelected()
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
                twoPlayer,
                modeHelp,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 700, 560);

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
            GameModel player1,
            GameModel player2,
            boolean player2Ai,
            Runnable backAction,
            boolean musicEnabled,
            boolean soundEnabled
    ) {

        gameCanvas =
                new Canvas(
                        player1.getCols() *
                                player1.getBlockSize(),
                        player1.getRows() *
                                player1.getBlockSize()
                );

        player1NextCanvas =
                new Canvas(96, 72);

        player2NextCanvas = null;
        secondGameCanvas = null;

        if (player2 != null) {
            secondGameCanvas =
                    new Canvas(
                            player2.getCols() *
                                    player2.getBlockSize(),
                            player2.getRows() *
                                    player2.getBlockSize()
                    );

            player2NextCanvas =
                    new Canvas(96, 72);
        }

        player1LevelLabel =
                new Label("Level: " + player1.getLevel());

        player1ScoreLabel =
                new Label("Score: 0");

        player1LinesLabel =
                new Label("Lines: 0");

        VBox player1Box =
                createPlayerBox(
                        "PLAYER 1 - HUMAN",
                        player1LevelLabel,
                        player1ScoreLabel,
                        player1LinesLabel,
                        gameCanvas,
                        player1NextCanvas,
                        player2 == null
                                ? "← → Move   ↑ Rotate   ↓ Down"
                                : "A/D Move   W Rotate   X Down"
                );

        HBox boards;

        if (player2 != null) {

            player2LevelLabel =
                    new Label("Level: " + player2.getLevel());

            player2ScoreLabel =
                    new Label("Score: 0");

            player2LinesLabel =
                    new Label("Lines: 0");

            VBox player2Box =
                    createPlayerBox(
                            player2Ai
                                    ? "PLAYER 2 - AI"
                                    : "PLAYER 2 - HUMAN",
                            player2LevelLabel,
                            player2ScoreLabel,
                            player2LinesLabel,
                            secondGameCanvas,
                            player2NextCanvas,
                            player2Ai
                                    ? "AI Controlled"
                                    : "←/→ Move   ↑ Rotate   ↓ Down"
                    );

            boards =
                    new HBox(
                            30,
                            player1Box,
                            player2Box
                    );

        } else {
            boards =
                    new HBox(player1Box);
        }

        boards.setAlignment(Pos.CENTER);

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

        HBox audioStatus =
                new HBox(
                        20,
                        musicLabel,
                        soundLabel
                );

        audioStatus.setAlignment(Pos.CENTER);

        Label controls = new Label(
                "P Pause   M Music   S Sound"
        );

        Button backButton =
                new Button("Back to Menu");

        backButton.setOnAction(
                event -> backAction.run()
        );
        backButton.setFocusTraversable(false);

        VBox root = new VBox(
                10,
                audioStatus,
                boards,
                controls,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setFocusTraversable(true);

        double boardWidth =
                gameCanvas.getWidth();

        if (secondGameCanvas != null) {
            boardWidth +=
                    secondGameCanvas.getWidth() + 80;
        }

        double sceneWidth =
                Math.max(
                        500,
                        boardWidth + 100
                );

        double sceneHeight =
                gameCanvas.getHeight() + 260;

        Scene scene =
                new Scene(
                        root,
                        sceneWidth,
                        sceneHeight
                );

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();

        drawGames(player1, player2);

        root.requestFocus();

        return scene;
    }

    private VBox createPlayerBox(
            String titleText,
            Label levelLabel,
            Label scoreLabel,
            Label linesLabel,
            Canvas canvas,
            Canvas nextCanvas,
            String controlsText
    ) {

        Label title =
                new Label(titleText);

        title.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        HBox details =
                new HBox(
                        15,
                        levelLabel,
                        scoreLabel,
                        linesLabel
                );

        details.setAlignment(Pos.CENTER);

        Label nextLabel =
                new Label("Next:");

        HBox nextBox =
                new HBox(
                        8,
                        nextLabel,
                        nextCanvas
                );

        nextBox.setAlignment(Pos.CENTER);

        Label playerControls =
                new Label(controlsText);

        VBox box =
                new VBox(
                        8,
                        title,
                        details,
                        nextBox,
                        canvas,
                        playerControls
                );

        box.setAlignment(Pos.CENTER);

        return box;
    }

    public void drawGames(
            GameModel player1,
            GameModel player2
    ) {

        if (player1 != null && gameCanvas != null) {
            player1LevelLabel.setText(
                    "Level: " + player1.getLevel()
            );
            player1ScoreLabel.setText(
                    "Score: " + player1.getScore()
            );
            player1LinesLabel.setText(
                    "Lines: " + player1.getLinesErased()
            );

            drawNextPiece(
                    player1NextCanvas,
                    player1
            );
            drawPlayer(gameCanvas, player1);
        }

        if (player2 != null && secondGameCanvas != null) {
            player2LevelLabel.setText(
                    "Level: " + player2.getLevel()
            );
            player2ScoreLabel.setText(
                    "Score: " + player2.getScore()
            );
            player2LinesLabel.setText(
                    "Lines: " + player2.getLinesErased()
            );

            drawNextPiece(
                    player2NextCanvas,
                    player2
            );
            drawPlayer(secondGameCanvas, player2);
        }
    }

    private void drawNextPiece(
            Canvas canvas,
            GameModel model
    ) {

        if (canvas == null || model == null) {
            return;
        }

        GraphicsContext gc =
                canvas.getGraphicsContext2D();

        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
        );

        int[][] shape =
                model.getNextPieceShape();

        int previewBlock = 18;
        double pieceWidth =
                shape[0].length * previewBlock;
        double pieceHeight =
                shape.length * previewBlock;

        double startX =
                (canvas.getWidth() - pieceWidth) / 2;
        double startY =
                (canvas.getHeight() - pieceHeight) / 2;

        gc.setFill(
                model.getNextPieceColor()
        );

        for (int row = 0;
             row < shape.length;
             row++) {

            for (int col = 0;
                 col < shape[row].length;
                 col++) {

                if (shape[row][col] == 0) {
                    continue;
                }

                gc.fillRect(
                        startX + col * previewBlock + 1,
                        startY + row * previewBlock + 1,
                        previewBlock - 2,
                        previewBlock - 2
                );
            }
        }
    }

    private void drawPlayer(
            Canvas canvas,
            GameModel model
    ) {

        GraphicsContext gc =
                canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);

        gc.fillRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
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
                    canvas,
                    gc,
                    "PAUSED",
                    Color.WHITE
            );
        }

        if (model.isGameOver()) {
            drawOverlay(
                    canvas,
                    gc,
                    "GAME OVER",
                    Color.RED
            );
        }
    }

    private void drawOverlay(
            Canvas canvas,
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
                canvas.getWidth(),
                canvas.getHeight()
        );

        gc.setFill(color);
        gc.setFont(Font.font(28));

        double textX =
                Math.max(
                        20,
                        canvas.getWidth() / 2 - 90
                );

        double textY =
                canvas.getHeight() / 2;

        gc.fillText(
                text,
                textX,
                textY
        );
    }

    public Optional<String> requestPlayerName(
            String player,
            int score
    ) {

        TextInputDialog dialog =
                new TextInputDialog(player);

        dialog.setTitle("New High Score");
        dialog.setHeaderText(
                player + " Score: " + score
        );
        dialog.setContentText(
                "Enter your name:"
        );
        dialog.initOwner(stage);

        dialog.setOnShown(event -> {
            TextField input =
                    dialog.getEditor();

            input.selectAll();
            input.requestFocus();
        });

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
