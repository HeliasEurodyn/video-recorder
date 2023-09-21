package com.ed.video_recorder.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getVideosPath() {
        return properties.getProperty("videosPath");
    }

    public static int getMaxRecordCount() {
        return Integer.parseInt(properties.getProperty("maxRecordCount"));
    }

    public static long getCaptureDurationMs() {
        return Long.parseLong(properties.getProperty("captureDurationMs"));
    }
}

