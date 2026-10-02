package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private final Path configPath =
            Path.of(
                    System.getProperty("user.dir"),
                    "data",
                    "config.json"
            );

    public GameConfig loadConfig() {

        if (!Files.exists(configPath)) {
            return GameConfig.defaultConfig();
        }

        try {
            String json =
                    Files.readString(configPath);

            GameConfig config =
                    gson.fromJson(
                            json,
                            GameConfig.class
                    );

            if (config == null) {
                return GameConfig.defaultConfig();
            }

            return config;

        } catch (IOException exception) {
            return GameConfig.defaultConfig();
        }
    }

    public void saveConfig(GameConfig config) {

        try {
            Files.createDirectories(
                    configPath.getParent()
            );

            Files.writeString(
                    configPath,
                    gson.toJson(config)
            );

        } catch (IOException exception) {
            System.out.println(
                    "Unable to save configuration."
            );
        }
    }
}
