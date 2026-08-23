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
}