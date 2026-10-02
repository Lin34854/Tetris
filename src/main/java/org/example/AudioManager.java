package org.example;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class AudioManager {

    private MediaPlayer backgroundPlayer;
    private AudioClip moveTurnSound;
    private AudioClip eraseLineSound;
    private AudioClip levelUpSound;
    private AudioClip gameFinishSound;

    private boolean musicEnabled;
    private boolean soundEnabled;

    private AudioManager() {
        loadAudio();
    }

    private static class Holder {
        private static final AudioManager INSTANCE =
                new AudioManager();
    }

    public static AudioManager getInstance() {
        return Holder.INSTANCE;
    }

    private void loadAudio() {

        URL background =
                getClass().getResource(
                        "/audio/background.mp3"
                );

        URL moveTurn =
                getClass().getResource(
                        "/audio/move-turn.wav"
                );

        URL eraseLine =
                getClass().getResource(
                        "/audio/erase-line.wav"
                );

        URL levelUp =
                getClass().getResource(
                        "/audio/level-up.wav"
                );

        URL gameFinish =
                getClass().getResource(
                        "/audio/game-finish.wav"
                );

        if (background != null) {
            Media media =
                    new Media(
                            background.toExternalForm()
                    );

            backgroundPlayer =
                    new MediaPlayer(media);

            backgroundPlayer.setCycleCount(
                    MediaPlayer.INDEFINITE
            );

            backgroundPlayer.setVolume(0.25);
        }

        if (moveTurn != null) {
            moveTurnSound =
                    new AudioClip(
                            moveTurn.toExternalForm()
                    );
        }

        if (eraseLine != null) {
            eraseLineSound =
                    new AudioClip(
                            eraseLine.toExternalForm()
                    );
        }

        if (levelUp != null) {
            levelUpSound =
                    new AudioClip(
                            levelUp.toExternalForm()
                    );
        }

        if (gameFinish != null) {
            gameFinishSound =
                    new AudioClip(
                            gameFinish.toExternalForm()
                    );
        }
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;

        if (musicEnabled) {
            playBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public void toggleMusic() {
        setMusicEnabled(!musicEnabled);
    }

    public void toggleSound() {
        soundEnabled = !soundEnabled;
    }

    public void playBackgroundMusic() {

        if (!musicEnabled ||
                backgroundPlayer == null) {
            return;
        }

        backgroundPlayer.play();
    }

    public void stopBackgroundMusic() {

        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
        }
    }

    public void playMoveTurn() {
        playSound(moveTurnSound);
    }

    public void playEraseLine() {
        playSound(eraseLineSound);
    }

    public void playLevelUp() {
        playSound(levelUpSound);
    }

    public void playGameFinish() {
        playSound(gameFinishSound);
    }

    private void playSound(AudioClip sound) {

        if (!soundEnabled || sound == null) {
            return;
        }

        sound.play();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
