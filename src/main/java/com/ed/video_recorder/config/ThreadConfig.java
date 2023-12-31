package com.ed.video_recorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadConfig {

    @Bean
    public ExecutorService executorService() {
        // Create an unbounded thread pool
        return Executors.newCachedThreadPool();
    }

}
