package com.game;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class SettingsManager {
    private static Properties settings = new Properties();

    static {
        loadSettings();
    }

    public static void init() {}

    private static void loadSettings() {
        try (InputStream in = SettingsManager.class.getClassLoader().getResourceAsStream("settings.properties")) {
            if (in == null) {
                throw new IOException("Cannot find 'settings.properties'");
            }
            settings.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSetting(String key) {
        return settings.getProperty(key);
    }
}
