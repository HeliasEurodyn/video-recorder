package com.ed.video_recorder.service;

import com.ed.video_recorder.dto.VideoDataDTO;
import com.ed.video_recorder.repository.VideoRecordRepository;
import com.ed.video_recorder.thread.VideoRecorderThread;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class VRecService {

    private final ExecutorService executorService;

    private final VideoRecordRepository videoRecordRepository;

    private final Map<String, Future<?>> runningTasks = new HashMap<>();
    private final Map<String, VideoRecorderThread> runningThreads = new HashMap<>();

    public VRecService(ExecutorService executorService,
                       VideoRecordRepository videoRecordRepository) {
        this.executorService = executorService;
        this.videoRecordRepository = videoRecordRepository;
    }

    public VideoDataDTO start(VideoDataDTO videoDataDTO) {
        var id = UUID.randomUUID().toString();
        videoDataDTO.setId(id);

        VideoRecorderThread videoRecorderThread = new VideoRecorderThread(videoDataDTO, videoRecordRepository);
        Future<?> future = executorService.submit(videoRecorderThread);
        runningTasks.put(id, future);
        runningThreads.put(id, videoRecorderThread);

        return videoDataDTO;
    }

    public VideoDataDTO stop(VideoDataDTO videoDataDTO) throws ExecutionException, InterruptedException {
        if(!this.runningTasks.containsKey(videoDataDTO.getId())){
            return videoDataDTO;
        }

        VideoRecorderThread completedThread = this.runningThreads.get(videoDataDTO.getId());
        completedThread.stop();

        Future<?> future = this.runningTasks.get(videoDataDTO.getId());
        future.cancel(true);

        this.runningTasks.remove(videoDataDTO.getId());
        this.runningThreads.remove(videoDataDTO.getId());

        return videoDataDTO;
    }

    public List<VideoDataDTO> list() {
        return runningThreads.values()
                .stream()
                .map(thread -> thread.getVideoDataDTO())
                .filter(videoDataDTO -> videoDataDTO != null)
                .collect(Collectors.toList());
    }
}
