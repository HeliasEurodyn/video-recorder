package com.ed.video_recorder.service;

import com.ed.video_recorder.config.AppConfig;
import com.ed.video_recorder.dto.StreamDTO;
import com.ed.video_recorder.mapper.StreamMapper;
import com.ed.video_recorder.model.Stream;
import com.ed.video_recorder.repository.RecordStreamRepository;
import com.ed.video_recorder.repository.StreamRepository;
import com.ed.video_recorder.thread.ThreadTupple;
import com.ed.video_recorder.thread.VideoRecorderThread;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class VRecService {

    private final ExecutorService executorService;

    private final StreamMapper streamMapper;

    private final RecordStreamRepository recordStreamRepository;

    private final StreamRepository streamRepository;

    private final Map<Long, ThreadTupple> runningTasks = new HashMap<>();


    public VRecService(ExecutorService executorService, StreamMapper streamMapper, RecordStreamRepository recordStreamRepository, StreamRepository streamRepository) {
        this.executorService = executorService;
        this.streamMapper = streamMapper;
        this.recordStreamRepository = recordStreamRepository;
        this.streamRepository = streamRepository;
    }

    public StreamDTO  saveAndStart(StreamDTO streamDTO){
        Stream stream = streamMapper.streamDTOToStream(streamDTO);
        Stream savedStream = streamRepository.save(stream);
        this.createFolder(stream.getId().toString());
        start(savedStream);
        return  streamMapper.streamToStreamDTO(savedStream);
    }

    public void start(Stream stream) {


        VideoRecorderThread videoRecorderThread = new VideoRecorderThread(stream, recordStreamRepository, streamRepository);
        Future<?> future = executorService.submit(videoRecorderThread);
        runningTasks.put(stream.getId(), new ThreadTupple(videoRecorderThread, future));

    }

    public StreamDTO stop(StreamDTO streamDTO) throws ExecutionException, InterruptedException {
        if (!this.runningTasks.containsKey(streamDTO.getId())) {
            return streamDTO;
        }

        ThreadTupple threadTupple = this.runningTasks.get(streamDTO.getId());
        threadTupple.getVideoRecorderThread().stop();
        threadTupple.getFuture().cancel(true);

        this.runningTasks.remove(streamDTO.getId());

        return streamDTO;
    }

    public List<StreamDTO> list() {
        List<Stream> allStreams = streamRepository.findAll();
        return allStreams.stream().map(streamMapper::streamToStreamDTO).collect(Collectors.toList());
    }

    public void createFolder(String folderName) {

        // Create a Path object representing the directory
        Path folder = Paths.get(AppConfig.getVideosPath() + folderName);

        // Check if the directory doesn't exist and create it
        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder); // Create the directory and any missing parent directories
                System.out.println("Folder created successfully.");
            } catch (IOException e) {
                System.err.println("Failed to create folder: " + e.getMessage());
            }
        } else {
            System.out.println("Folder already exists.");
        }
    }

}
