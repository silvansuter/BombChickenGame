package com.game;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class SettingsManager {
    private Properties settings;

    public SettingsManager() {
        loadSettings();
    }

    private void loadSettings() {
        settings = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("settings.properties")) {
            if (in == null) {
                throw new IOException("Cannot find 'settings.properties'");
            }
            settings.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSetting(String key) {
        return settings.getProperty(key);
    }
}

