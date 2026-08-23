package org.example;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class Main extends Application {

    private static final int ROWS = 20;
    private static final int COLS = 10;
    private static final int BLOCK = 30;

    // Automatic drop interval: 700 ms
    private static final long DROP_INTERVAL = 700_000_000L;

    private final int[][] board = new int[ROWS][COLS];

    private int[][] currentPiece;
    private int currentPieceType;

    private int pieceRow;
    private int pieceCol;

    private boolean paused = false;
    private boolean gameOver = false;

    private Canvas gameCanvas;
    private AnimationTimer gameTimer;

    private long lastDropTime = 0;

    // Used to create smooth downward movement
    private double smoothOffset = 0;

    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        showSplash(stage);
    }

    // =====================================================
    // SPLASH SCREEN
    // =====================================================

    private void showSplash(Stage stage) {

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

        Scene scene = new Scene(root, 700, 450);

        stage.setTitle("Tetris - Milestone 1");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        PauseTransition delay =
                new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(
                e -> showMainMenu(stage)
        );

        delay.play();
    }

    // =====================================================
    // MAIN MENU
    // =====================================================

    private void showMainMenu(Stage stage) {

        if (gameTimer != null) {
            gameTimer.stop();
        }

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
                e -> startGame(stage)
        );

        configurationButton.setOnAction(
                e -> showConfiguration(stage)
        );

        highScoresButton.setOnAction(
                e -> showHighScores(stage)
        );

        exitButton.setOnAction(
                e -> showExitConfirmation(stage)
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
        stage.centerOnScreen();
    }

    // =====================================================
    // CONFIGURATION SCREEN
    // =====================================================

    private void showConfiguration(Stage stage) {

        Label title =
                new Label("Configuration");

        title.setStyle(
                "-fx-font-size: 30px; " +
                        "-fx-font-weight: bold;"
        );

        Label fieldSize =
                new Label("Field Size: 10 x 20");

        Slider levelSlider =
                new Slider(1, 10, 1);

        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        levelSlider.setBlockIncrement(1);
        levelSlider.setSnapToTicks(true);
        levelSlider.setPrefWidth(300);

        Label levelLabel =
                new Label("Level: 1");

        levelSlider.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            int level =
                                    (int) Math.round(
                                            newValue.doubleValue()
                                    );

                            levelLabel.setText(
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

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(150);

        backButton.setOnAction(
                e -> showMainMenu(stage)
        );

        VBox root = new VBox(
                15,
                title,
                fieldSize,
                levelLabel,
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
        stage.centerOnScreen();
    }

    // =====================================================
    // HIGH SCORES SCREEN
    // =====================================================

    private void showHighScores(Stage stage) {

        Label title =
                new Label("High Scores");

        title.setStyle(
                "-fx-font-size: 30px; " +
                        "-fx-font-weight: bold;"
        );

        VBox scores =
                new VBox(8);

        scores.setAlignment(Pos.CENTER);

        String[] highScores = {
                "1. Player 1 - 10100",
                "2. Player 2 - 9200",
                "3. Player 3 - 8300",
                "4. Player 4 - 7400",
                "5. Player 5 - 6500",
                "6. Player 6 - 5600",
                "7. Player 7 - 4700",
                "8. Player 8 - 3800",
                "9. Player 9 - 2900",
                "10. Player 10 - 2000"
        };

// Enhanced for loop
        for (String scoreText : highScores) {
            scores.getChildren().add(new Label(scoreText));
        }

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(150);

        backButton.setOnAction(
                e -> showMainMenu(stage)
        );

        VBox root = new VBox(
                20,
                title,
                scores,
                backButton
        );

        root.setAlignment(Pos.CENTER);

        Scene scene =
                new Scene(root, 700, 500);

        stage.setScene(scene);
        stage.centerOnScreen();
    }

    // =====================================================
    // START GAME
    // =====================================================

    private void startGame(Stage stage) {

        clearBoard();

        paused = false;
        gameOver = false;
        smoothOffset = 0;
        lastDropTime = 0;

        gameCanvas =
                new Canvas(
                        COLS * BLOCK,
                        ROWS * BLOCK
                );

        Label controls = new Label(
                "← → Move   ↑ Rotate   ↓ Down   P Pause"
        );

        Button backButton =
                new Button("Back to Menu");

        backButton.setOnAction(e -> {

            if (gameTimer != null) {
                gameTimer.stop();
            }

            showMainMenu(stage);
        });

        VBox root = new VBox(
                10,
                gameCanvas,
                controls,
                backButton
        );

        root.setAlignment(Pos.CENTER);

        Scene scene =
                new Scene(root, 500, 720);

        stage.setScene(scene);
        stage.centerOnScreen();

        spawnPiece();

        scene.setOnKeyPressed(event -> {

            if (gameOver) {
                return;
            }

            if (event.getCode() == KeyCode.P) {

                paused = !paused;

                // Reset the timer so the piece does not drop instantly after resuming
                lastDropTime = 0;

                drawGame();

                return;
            }

            if (paused) {
                return;
            }

            switch (event.getCode()) {

                case LEFT -> {
                    moveHorizontal(-1);
                }

                case RIGHT -> {
                    moveHorizontal(1);
                }

                case UP -> {
                    rotatePiece();
                }

                case DOWN -> {
                    manualMoveDown();
                }

                default -> {
                }
            }

            drawGame();
        });

        startGameLoop();

        root.requestFocus();
    }

    // =====================================================
    // GAME LOOP AND SMOOTH FALLING
    // =====================================================

    private void startGameLoop() {

        gameTimer =
                new AnimationTimer() {

                    @Override
                    public void handle(long now) {

                        if (paused || gameOver) {
                            drawGame();
                            return;
                        }

                        if (lastDropTime == 0) {
                            lastDropTime = now;
                        }

                        long elapsed =
                                now - lastDropTime;

                        /*
                         * If the piece can move down one more row,
                         * smoothly animate it to the next row
                         * during the drop interval.
                         */
                        if (canMove(
                                currentPiece,
                                pieceRow + 1,
                                pieceCol
                        )) {

                            smoothOffset =
                                    Math.min(
                                            1.0,
                                            (double) elapsed
                                                    / DROP_INTERVAL
                                    );

                            if (elapsed >= DROP_INTERVAL) {

                                pieceRow++;

                                smoothOffset = 0;

                                lastDropTime = now;
                            }

                        } else {

                            /*
                             * If the piece has reached the bottom,
                             * wait until the current drop interval
                             * finishes, then lock the piece in place.
                             */
                            smoothOffset = 0;

                            if (elapsed >= DROP_INTERVAL) {

                                lockPiece();

                                clearFullRows();

                                spawnPiece();

                                lastDropTime = now;
                            }
                        }

                        drawGame();
                    }
                };

        gameTimer.start();
    }

    // =====================================================
    // SPAWN NEW TETROMINO
    // =====================================================

    private void spawnPiece() {

        int type =
                random.nextInt(7);

        currentPieceType =
                type + 1;

        currentPiece =
                switch (type) {

                    // I Piece
                    case 0 -> new int[][]{
                            {1, 1, 1, 1}
                    };

                    // O Piece
                    case 1 -> new int[][]{
                            {1, 1},
                            {1, 1}
                    };

                    // T Piece
                    case 2 -> new int[][]{
                            {0, 1, 0},
                            {1, 1, 1}
                    };

                    // J Piece
                    case 3 -> new int[][]{
                            {1, 0, 0},
                            {1, 1, 1}
                    };

                    // L Piece
                    case 4 -> new int[][]{
                            {0, 0, 1},
                            {1, 1, 1}
                    };

                    // S Piece
                    case 5 -> new int[][]{
                            {0, 1, 1},
                            {1, 1, 0}
                    };

                    // Z Piece
                    default -> new int[][]{
                            {1, 1, 0},
                            {0, 1, 1}
                    };
                };

        pieceRow = 0;

        pieceCol =
                COLS / 2 -
                        currentPiece[0].length / 2;

        smoothOffset = 0;
        lastDropTime = 0;

        if (!canMove(
                currentPiece,
                pieceRow,
                pieceCol
        )) {

            gameOver = true;

            if (gameTimer != null) {
                gameTimer.stop();
            }

            drawGame();
        }
    }

    // =====================================================
    // HORIZONTAL MOVEMENT
    // =====================================================

    private void moveHorizontal(int amount) {

        int newCol =
                pieceCol + amount;

        if (canMove(
                currentPiece,
                pieceRow,
                newCol
        )) {

            pieceCol = newCol;
        }
    }

    // =====================================================
    // MANUAL DOWNWARD MOVEMENT
    // =====================================================

    private void manualMoveDown() {

        if (canMove(
                currentPiece,
                pieceRow + 1,
                pieceCol
        )) {

            pieceRow++;

            smoothOffset = 0;

            lastDropTime = 0;

        } else {

            lockPiece();

            clearFullRows();

            spawnPiece();
        }
    }

    // =====================================================
    // ROTATION
    // =====================================================

    private void rotatePiece() {

        int rows =
                currentPiece.length;

        int cols =
                currentPiece[0].length;

        int[][] rotated =
                new int[cols][rows];

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < cols; c++) {

                rotated[c][rows - 1 - r] =
                        currentPiece[r][c];
            }
        }

        if (canMove(
                rotated,
                pieceRow,
                pieceCol
        )) {

            currentPiece = rotated;
        }
    }

    // =====================================================
    // COLLISION DETECTION
    // =====================================================

    private boolean canMove(
            int[][] piece,
            int newRow,
            int newCol
    ) {

        for (int r = 0;
             r < piece.length;
             r++) {

            for (int c = 0;
                 c < piece[r].length;
                 c++) {

                if (piece[r][c] == 0) {
                    continue;
                }

                int boardRow =
                        newRow + r;

                int boardCol =
                        newCol + c;

                if (
                        boardRow < 0 ||
                                boardRow >= ROWS ||
                                boardCol < 0 ||
                                boardCol >= COLS
                ) {

                    return false;
                }

                if (
                        board[boardRow][boardCol] != 0
                ) {

                    return false;
                }
            }
        }

        return true;
    }

    // =====================================================
    // LOCK PIECE ONTO THE BOARD
    // =====================================================

    private void lockPiece() {

        for (int r = 0;
             r < currentPiece.length;
             r++) {

            for (int c = 0;
                 c < currentPiece[r].length;
                 c++) {

                if (currentPiece[r][c] == 0) {
                    continue;
                }

                int row =
                        pieceRow + r;

                int col =
                        pieceCol + c;

                if (
                        row >= 0 &&
                                row < ROWS &&
                                col >= 0 &&
                                col < COLS
                ) {

                    /*
                     * Store the specific tetromino type
                     * so each locked piece keeps its own colour.
                     */
                    board[row][col] =
                            currentPieceType;
                }
            }
        }
    }

    // =====================================================
    // CLEAR FULL ROWS
    // =====================================================

    private void clearFullRows() {

        for (int row =
             ROWS - 1;
             row >= 0;
             row--) {

            boolean full = true;

            for (int col = 0;
                 col < COLS;
                 col++) {

                if (board[row][col] == 0) {

                    full = false;

                    break;
                }
            }

            if (full) {

                for (int r = row;
                     r > 0;
                     r--) {

                    for (int c = 0;
                         c < COLS;
                         c++) {

                        board[r][c] =
                                board[r - 1][c];
                    }
                }

                for (int c = 0;
                     c < COLS;
                     c++) {

                    board[0][c] = 0;
                }

                /*
                 * Check the same row again because
                 * multiple full rows may be cleared at once.
                 */
                row++;
            }
        }
    }

    // =====================================================
    // TETROMINO COLOURS
    // =====================================================

    private Color getPieceColor(int type) {

        return switch (type) {

            case 1 -> Color.CYAN;
            case 2 -> Color.GOLD;
            case 3 -> Color.MEDIUMPURPLE;
            case 4 -> Color.DODGERBLUE;
            case 5 -> Color.ORANGE;
            case 6 -> Color.LIMEGREEN;
            case 7 -> Color.RED;

            default -> Color.GRAY;
        };
    }

    // =====================================================
    // DRAW GAME
    // =====================================================

    private void drawGame() {

        if (gameCanvas == null) {
            return;
        }

        GraphicsContext gc =
                gameCanvas.getGraphicsContext2D();

        // Draw the game background
        gc.setFill(Color.BLACK);

        gc.fillRect(
                0,
                0,
                gameCanvas.getWidth(),
                gameCanvas.getHeight()
        );

        // Draw locked blocks and the grid
        for (int row = 0;
             row < ROWS;
             row++) {

            for (int col = 0;
                 col < COLS;
                 col++) {

                double x =
                        col * BLOCK;

                double y =
                        row * BLOCK;

                if (board[row][col] != 0) {

                    gc.setFill(
                            getPieceColor(
                                    board[row][col]
                            )
                    );

                    gc.fillRect(
                            x + 1,
                            y + 1,
                            BLOCK - 2,
                            BLOCK - 2
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
                        BLOCK,
                        BLOCK
                );
            }
        }

        // Draw the current falling tetromino
        if (
                currentPiece != null &&
                        !gameOver
        ) {

            gc.setFill(
                    getPieceColor(
                            currentPieceType
                    )
            );

            for (int r = 0;
                 r < currentPiece.length;
                 r++) {

                for (int c = 0;
                     c < currentPiece[r].length;
                     c++) {

                    if (
                            currentPiece[r][c] == 0
                    ) {

                        continue;
                    }

                    double x =
                            (pieceCol + c)
                                    * BLOCK;

                    /*
                     * pieceRow + smoothOffset allows the piece
                     * to visibly move between two adjacent rows.
                     */
                    double y =
                            (
                                    pieceRow +
                                            r +
                                            smoothOffset
                            ) * BLOCK;

                    gc.fillRect(
                            x + 1,
                            y + 1,
                            BLOCK - 2,
                            BLOCK - 2
                    );

                    gc.setStroke(Color.WHITE);

                    gc.strokeRect(
                            x + 1,
                            y + 1,
                            BLOCK - 2,
                            BLOCK - 2
                    );
                }
            }
        }

        // Draw the pause overlay
        if (paused) {

            gc.setFill(
                    Color.rgb(
                            0,
                            0,
                            0,
                            0.65
                    )
            );

            gc.fillRect(
                    0,
                    0,
                    gameCanvas.getWidth(),
                    gameCanvas.getHeight()
            );

            gc.setFill(Color.WHITE);

            gc.setFont(
                    Font.font(32)
            );

            gc.fillText(
                    "PAUSED",
                    85,
                    300
            );
        }

        // Draw the game over overlay
        if (gameOver) {

            gc.setFill(
                    Color.rgb(
                            0,
                            0,
                            0,
                            0.75
                    )
            );

            gc.fillRect(
                    0,
                    0,
                    gameCanvas.getWidth(),
                    gameCanvas.getHeight()
            );

            gc.setFill(Color.RED);

            gc.setFont(
                    Font.font(30)
            );

            gc.fillText(
                    "GAME OVER",
                    55,
                    300
            );
        }
    }

    // =====================================================
    // CLEAR BOARD
    // =====================================================

    private void clearBoard() {

        for (int row = 0;
             row < ROWS;
             row++) {

            for (int col = 0;
                 col < COLS;
                 col++) {

                board[row][col] = 0;
            }
        }
    }

    // =====================================================
    // EXIT CONFIRMATION
    // =====================================================

    private void showExitConfirmation(
            Stage stage
    ) {

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

        alert.showAndWait()
                .ifPresent(result -> {

                    if (result == yesButton) {

                        stage.close();

                    } else {

                        showMainMenu(stage);
                    }
                });
    }

    public static void main(String[] args) {
        launch(args);
    }
}