package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.EnumSet;
import java.util.Set;

public class GameController {

    private static final long HORIZONTAL_REPEAT_NS = 90_000_000L;
    private static final long DOWN_REPEAT_NS = 50_000_000L;

    private final GameModel player1;
    private GameModel player2;

    private final GameView view;
    private final ConfigManager configManager;
    private final HighScoreManager highScoreManager;
    private final AudioManager audioManager;

    private GameConfig config;
    private AnimationTimer gameTimer;

    private boolean player1HighScoreHandled;
    private boolean player2HighScoreHandled;

    private int player1PreviousLines;
    private int player2PreviousLines;
    private int player1PreviousLevel;
    private int player2PreviousLevel;

    private final Set<KeyCode> pressedKeys =
            EnumSet.noneOf(KeyCode.class);

    private long player1HorizontalTime;
    private long player2HorizontalTime;
    private long player1DownTime;
    private long player2DownTime;

    public GameController(
            GameModel model,
            GameView view,
            ConfigManager configManager,
            HighScoreManager highScoreManager
    ) {
        this.player1 = model;
        this.view = view;
        this.configManager = configManager;
        this.highScoreManager = highScoreManager;
        this.audioManager = AudioManager.getInstance();
        this.config = configManager.loadConfig();

        audioManager.setMusicEnabled(
                config.musicEnabled()
        );

        audioManager.setSoundEnabled(
                config.soundEnabled()
        );
    }

    public void start() {
        view.showSplash(this::showMainMenu);
    }

    private void showMainMenu() {

        stopGameTimer();

        view.showMainMenu(
                this::startGame,
                this::showConfiguration,
                this::showHighScores,
                this::exitGame
        );
    }

    private void showConfiguration() {

        view.showConfiguration(
                config,
                updatedConfig -> {
                    config = updatedConfig;
                    configManager.saveConfig(config);

                    audioManager.setMusicEnabled(
                            config.musicEnabled()
                    );

                    audioManager.setSoundEnabled(
                            config.soundEnabled()
                    );

                    showMainMenu();
                }
        );
    }

    private void showHighScores() {

        view.showHighScores(
                highScoreManager.getTopScores(),
                () -> {
                    if (view.confirmClearScores()) {
                        highScoreManager.clearScores();
                        player1HighScoreHandled = false;
                        player2HighScoreHandled = false;
                        showHighScores();
                    }
                },
                this::showMainMenu
        );
    }

    private void startGame() {

        stopGameTimer();

        PieceSequence sequence =
                new PieceSequence();

        player1.reset(
                config,
                sequence,
                false
        );

        boolean multiplayer =
                config.twoPlayerEnabled() ||
                        config.aiPlayEnabled();

        boolean player2Ai =
                config.aiPlayEnabled();

        if (multiplayer) {
            player2 =
                    new GameModel();

            player2.reset(
                    config,
                    sequence,
                    player2Ai
            );
        } else {
            player2 = null;
        }

        player1HighScoreHandled = false;
        player2HighScoreHandled = false;

        player1PreviousLines = 0;
        player2PreviousLines = 0;
        player1PreviousLevel =
                player1.getLevel();
        player2PreviousLevel =
                player2 == null
                        ? 0
                        : player2.getLevel();

        Scene scene =
                view.showGame(
                        player1,
                        player2,
                        player2Ai,
                        this::showMainMenu,
                        audioManager.isMusicEnabled(),
                        audioManager.isSoundEnabled()
                );

        pressedKeys.clear();
        player1HorizontalTime = 0;
        player2HorizontalTime = 0;
        player1DownTime = 0;
        player2DownTime = 0;

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            KeyCode code = event.getCode();
            boolean firstPress = pressedKeys.add(code);

            if (!firstPress) {
                return;
            }

            if (code == KeyCode.M) {
                audioManager.toggleMusic();
                saveAudioSettings();
                view.updateAudioStatus(
                        audioManager.isMusicEnabled(),
                        audioManager.isSoundEnabled()
                );
                return;
            }

            if (code == KeyCode.S) {
                audioManager.toggleSound();
                saveAudioSettings();
                view.updateAudioStatus(
                        audioManager.isMusicEnabled(),
                        audioManager.isSoundEnabled()
                );
                return;
            }

            if (code == KeyCode.P) {
                boolean pause =
                        !player1.isPaused();

                player1.setPaused(pause);

                if (player2 != null) {
                    player2.setPaused(pause);
                }

                view.drawGames(
                        player1,
                        player2
                );
                return;
            }

            if (player1.isPaused()) {
                return;
            }

            boolean moved = handleInitialMovement(code);
            boolean rotated = false;

            if (player2 == null) {
                if (code == KeyCode.UP &&
                        !player1.isGameOver()) {
                    player1.rotatePiece();
                    rotated = true;
                }
            } else {
                if (code == KeyCode.W &&
                        !player1.isGameOver()) {
                    player1.rotatePiece();
                    rotated = true;
                }

                if (!player2.isAiControlled() &&
                        code == KeyCode.UP &&
                        !player2.isGameOver()) {
                    player2.rotatePiece();
                    rotated = true;
                }
            }

            if (rotated || moved) {
                audioManager.playMoveTurn();
            }

            event.consume();

            handleGameSounds();
            view.drawGames(
                    player1,
                    player2
            );
            handleGameOver();
        });

        scene.addEventFilter(
                KeyEvent.KEY_RELEASED,
                event -> {
                    pressedKeys.remove(event.getCode());
                    event.consume();
                }
        );

        gameTimer =
                new AnimationTimer() {

                    @Override
                    public void handle(long now) {

                        handleHeldKeys(now);

                        player1.update(now);

                        if (player2 != null) {
                            player2.update(now);
                        }

                        handleGameSounds();

                        view.drawGames(
                                player1,
                                player2
                        );

                        handleGameOver();
                    }
                };

        gameTimer.start();
    }


    private boolean handleInitialMovement(KeyCode code) {

        long now = System.nanoTime();

        if (player2 == null) {
            if (player1.isGameOver()) {
                return false;
            }

            if (code == KeyCode.LEFT) {
                player1.moveHorizontal(-1);
                player1HorizontalTime = now;
                return true;
            }

            if (code == KeyCode.RIGHT) {
                player1.moveHorizontal(1);
                player1HorizontalTime = now;
                return true;
            }

            if (code == KeyCode.DOWN) {
                player1.manualMoveDown();
                player1DownTime = now;
                return false;
            }

            return false;
        }

        if (!player1.isGameOver()) {
            if (code == KeyCode.A) {
                player1.moveHorizontal(-1);
                player1HorizontalTime = now;
                return true;
            }

            if (code == KeyCode.D) {
                player1.moveHorizontal(1);
                player1HorizontalTime = now;
                return true;
            }

            if (code == KeyCode.X) {
                player1.manualMoveDown();
                player1DownTime = now;
                return false;
            }
        }

        if (!player2.isAiControlled() &&
                !player2.isGameOver()) {

            if (code == KeyCode.LEFT) {
                player2.moveHorizontal(-1);
                player2HorizontalTime = now;
                return true;
            }

            if (code == KeyCode.RIGHT) {
                player2.moveHorizontal(1);
                player2HorizontalTime = now;
                return true;
            }

            if (code == KeyCode.DOWN) {
                player2.manualMoveDown();
                player2DownTime = now;
                return false;
            }
        }

        return false;
    }

    private void handleHeldKeys(long now) {

        if (player1.isPaused()) {
            return;
        }

        if (player2 == null) {
            handleHeldSinglePlayer(now);
            return;
        }

        handleHeldPlayerOne(now);

        if (!player2.isAiControlled()) {
            handleHeldPlayerTwo(now);
        }
    }

    private void handleHeldSinglePlayer(long now) {

        if (player1.isGameOver()) {
            return;
        }

        boolean left = pressedKeys.contains(KeyCode.LEFT);
        boolean right = pressedKeys.contains(KeyCode.RIGHT);

        if (left != right &&
                now - player1HorizontalTime >= HORIZONTAL_REPEAT_NS) {

            player1.moveHorizontal(left ? -1 : 1);
            player1HorizontalTime = now;
            audioManager.playMoveTurn();
        }

        if (pressedKeys.contains(KeyCode.DOWN) &&
                now - player1DownTime >= DOWN_REPEAT_NS) {

            player1.manualMoveDown();
            player1DownTime = now;
        }
    }

    private void handleHeldPlayerOne(long now) {

        if (player1.isGameOver()) {
            return;
        }

        boolean left = pressedKeys.contains(KeyCode.A);
        boolean right = pressedKeys.contains(KeyCode.D);

        if (left != right &&
                now - player1HorizontalTime >= HORIZONTAL_REPEAT_NS) {

            player1.moveHorizontal(left ? -1 : 1);
            player1HorizontalTime = now;
            audioManager.playMoveTurn();
        }

        if (pressedKeys.contains(KeyCode.X) &&
                now - player1DownTime >= DOWN_REPEAT_NS) {

            player1.manualMoveDown();
            player1DownTime = now;
        }
    }

    private void handleHeldPlayerTwo(long now) {

        if (player2 == null ||
                player2.isGameOver()) {
            return;
        }

        boolean left = pressedKeys.contains(KeyCode.LEFT);
        boolean right = pressedKeys.contains(KeyCode.RIGHT);

        if (left != right &&
                now - player2HorizontalTime >= HORIZONTAL_REPEAT_NS) {

            player2.moveHorizontal(left ? -1 : 1);
            player2HorizontalTime = now;
            audioManager.playMoveTurn();
        }

        if (pressedKeys.contains(KeyCode.DOWN) &&
                now - player2DownTime >= DOWN_REPEAT_NS) {

            player2.manualMoveDown();
            player2DownTime = now;
        }
    }

    private void handleGameSounds() {

        if (player1.getLinesErased() >
                player1PreviousLines) {

            audioManager.playEraseLine();
            player1PreviousLines =
                    player1.getLinesErased();
        }

        if (player1.getLevel() >
                player1PreviousLevel) {

            audioManager.playLevelUp();
            player1PreviousLevel =
                    player1.getLevel();
        }

        if (player2 != null) {

            if (player2.getLinesErased() >
                    player2PreviousLines) {

                audioManager.playEraseLine();
                player2PreviousLines =
                        player2.getLinesErased();
            }

            if (player2.getLevel() >
                    player2PreviousLevel) {

                audioManager.playLevelUp();
                player2PreviousLevel =
                        player2.getLevel();
            }
        }
    }

    private void handleGameOver() {

        handlePlayerOneGameOver();
        handlePlayerTwoGameOver();
    }

    private void handlePlayerOneGameOver() {

        if (!player1.isGameOver() ||
                player1HighScoreHandled) {
            return;
        }

        player1HighScoreHandled = true;
        audioManager.playGameFinish();

        int score = player1.getScore();

        if (highScoreManager.qualifies(score)) {
            Platform.runLater(() ->
                    view.requestPlayerName(
                            "Player 1",
                            score
                    ).ifPresent(name ->
                            highScoreManager.addScore(
                                    name,
                                    score
                            )
                    )
            );
        }
    }

    private void handlePlayerTwoGameOver() {

        if (player2 == null ||
                !player2.isGameOver() ||
                player2HighScoreHandled) {
            return;
        }

        player2HighScoreHandled = true;
        audioManager.playGameFinish();

        int score = player2.getScore();

        if (highScoreManager.qualifies(score)) {

            if (player2.isAiControlled()) {
                highScoreManager.addScore(
                        "AI",
                        score
                );
            } else {
                Platform.runLater(() ->
                        view.requestPlayerName(
                                "Player 2",
                                score
                        ).ifPresent(name ->
                                highScoreManager.addScore(
                                        name,
                                        score
                                )
                        )
                );
            }
        }
    }

    private void saveAudioSettings() {

        config = new GameConfig(
                config.fieldWidth(),
                config.fieldHeight(),
                config.level(),
                audioManager.isMusicEnabled(),
                audioManager.isSoundEnabled(),
                config.aiPlayEnabled(),
                config.twoPlayerEnabled()
        );

        configManager.saveConfig(config);
    }

    private void exitGame() {

        if (view.confirmExit()) {
            stopGameTimer();
            audioManager.stopBackgroundMusic();
            view.close();
        }
    }

    private void stopGameTimer() {

        pressedKeys.clear();

        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }
}
