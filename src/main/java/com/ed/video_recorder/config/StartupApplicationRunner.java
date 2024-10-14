package com.ed.video_recorder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StartupApplicationRunner implements ApplicationRunner {

    @Value("${videosPath}")
    private String videosPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
       createDirectoryIfNotExists();
    }

    private void createDirectoryIfNotExists() {
        Path path = Paths.get(videosPath);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
                System.out.println("Directory created: " + path.toAbsolutePath());
            } else {
                System.out.println("Directory already exists: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
