package org.example;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class GameController {

    private final GameModel model;
    private final GameView view;
    private final ConfigManager configManager;
    private final HighScoreManager highScoreManager;
    private final AudioManager audioManager;

    private GameConfig config;
    private AnimationTimer gameTimer;
    private boolean highScoreHandled;
    private int previousLinesErased;
    private int previousLevel;

    public GameController(
            GameModel model,
            GameView view,
            ConfigManager configManager,
            HighScoreManager highScoreManager
    ) {
        this.model = model;
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
                        showHighScores();
                    }
                },
                this::showMainMenu
        );
    }

    private void startGame() {

        stopGameTimer();

        model.reset(config);
        highScoreHandled = false;
        previousLinesErased = 0;
        previousLevel = model.getLevel();

        Scene scene =
                view.showGame(
                        model,
                        this::showMainMenu,
                        audioManager.isMusicEnabled(),
                        audioManager.isSoundEnabled()
                );

        scene.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.M) {
                audioManager.toggleMusic();
                saveAudioSettings();
                view.updateAudioStatus(
                        audioManager.isMusicEnabled(),
                        audioManager.isSoundEnabled()
                );
                return;
            }

            if (event.getCode() == KeyCode.S) {
                audioManager.toggleSound();
                saveAudioSettings();
                view.updateAudioStatus(
                        audioManager.isMusicEnabled(),
                        audioManager.isSoundEnabled()
                );
                return;
            }

            if (event.getCode() == KeyCode.P) {
                model.togglePause();
                view.drawGame(model);
                return;
            }

            if (model.isPaused() ||
                    model.isGameOver()) {
                return;
            }

            switch (event.getCode()) {

                case LEFT -> {
                    model.moveHorizontal(-1);
                    audioManager.playMoveTurn();
                }

                case RIGHT -> {
                    model.moveHorizontal(1);
                    audioManager.playMoveTurn();
                }

                case UP -> {
                    model.rotatePiece();
                    audioManager.playMoveTurn();
                }

                case DOWN ->
                        model.manualMoveDown();

                default -> {
                }
            }

            handleGameSounds();
            view.drawGame(model);
            handleGameOver();
        });

        gameTimer =
                new AnimationTimer() {

                    @Override
                    public void handle(long now) {

                        model.update(now);
                        handleGameSounds();
                        view.drawGame(model);
                        handleGameOver();
                    }
                };

        gameTimer.start();
    }

    private void handleGameSounds() {

        if (model.getLinesErased() >
                previousLinesErased) {

            audioManager.playEraseLine();
            previousLinesErased =
                    model.getLinesErased();
        }

        if (model.getLevel() > previousLevel) {
            audioManager.playLevelUp();
            previousLevel = model.getLevel();
        }
    }

    private void handleGameOver() {

        if (!model.isGameOver() ||
                highScoreHandled) {
            return;
        }

        highScoreHandled = true;
        audioManager.playGameFinish();

        if (highScoreManager.qualifies(
                model.getScore()
        )) {

            view.requestPlayerName(
                    model.getScore()
            ).ifPresent(name ->
                    highScoreManager.addScore(
                            name,
                            model.getScore()
                    )
            );
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
                config.extendedModeEnabled()
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

        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }
}
