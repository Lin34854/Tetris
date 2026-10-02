package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {

    private final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private final Path scorePath =
            Path.of(
                    System.getProperty("user.dir"),
                    "data",
                    "highscores.json"
            );

    private final List<HighScore> scores =
            new ArrayList<>();

    public HighScoreManager() {
        loadScores();
    }

    public List<HighScore> getTopScores() {

        return scores.stream()
                .sorted(
                        Comparator.comparingInt(
                                HighScore::score
                        ).reversed()
                )
                .limit(10)
                .toList();
    }

    public boolean qualifies(int score) {

        List<HighScore> topScores =
                getTopScores();

        return score > 0 &&
                (topScores.size() < 10 ||
                        score > topScores.get(
                                topScores.size() - 1
                        ).score());
    }

    public void addScore(
            String name,
            int score
    ) {

        String playerName =
                name == null || name.isBlank()
                        ? "Player"
                        : name.trim();

        scores.add(
                new HighScore(
                        playerName,
                        score
                )
        );

        List<HighScore> topScores =
                getTopScores();

        scores.clear();
        scores.addAll(topScores);

        saveScores();
    }

    public void clearScores() {
        scores.clear();
        saveScores();
    }

    private void loadScores() {

        if (!Files.exists(scorePath)) {
            return;
        }

        try {
            String json =
                    Files.readString(scorePath);

            Type scoreListType =
                    new TypeToken<List<HighScore>>() {
                    }.getType();

            List<HighScore> loadedScores =
                    gson.fromJson(
                            json,
                            scoreListType
                    );

            if (loadedScores != null) {
                scores.addAll(loadedScores);
            }

        } catch (IOException exception) {
            System.out.println(
                    "Unable to load high scores."
            );
        }
    }

    private void saveScores() {

        try {
            Files.createDirectories(
                    scorePath.getParent()
            );

            Files.writeString(
                    scorePath,
                    gson.toJson(scores)
            );

        } catch (IOException exception) {
            System.out.println(
                    "Unable to save high scores."
            );
        }
    }
}
