package org.example;

public record GameConfig(
        int fieldWidth,
        int fieldHeight,
        int level,
        boolean musicEnabled,
        boolean soundEnabled,
        boolean aiPlayEnabled,
        boolean extendedModeEnabled
) {
    public static GameConfig defaultConfig() {
        return new GameConfig(
                10,
                20,
                1,
                false,
                false,
                false,
                false
        );
    }
}
