package com.ed.video_recorder.service;

import com.ed.video_recorder.config.AppConfig;
import com.ed.video_recorder.dto.RecordedStreamDTO;
import com.ed.video_recorder.dto.StreamDTO;
import com.ed.video_recorder.mapper.StreamMapper;
import com.ed.video_recorder.model.RecordedStream;
import com.ed.video_recorder.model.Stream;
import com.ed.video_recorder.repository.RecordStreamRepository;
import com.ed.video_recorder.repository.StreamRepository;
import com.ed.video_recorder.thread.ThreadTupple;
import com.ed.video_recorder.thread.VideoRecorderThread;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Optional<Stream> existingStream = Optional.empty();
        if(stream.getId() != null){
            existingStream = streamRepository.findById(stream.getId());
        }

        if (existingStream.isPresent()) {
            Stream savedStream = existingStream.get();
            if(savedStream.getStatus().equals("running")){
                return streamMapper.streamToStreamDTO(savedStream);
            }
            else {
                savedStream.setStatus("running");
                streamRepository.save(savedStream);
                start(savedStream);
                return streamMapper.streamToStreamDTO(savedStream);
                }

        } else {
            stream.setStatus("running");
            Stream savedStream = streamRepository.save(stream);
            this.createFolder(savedStream.getId().toString());
            start(savedStream);
            return streamMapper.streamToStreamDTO(savedStream);
        }
    }

    public void start(Stream stream) {
//        // Create a folder for existing recordings
//        createFolder(stream.getId().toString());
//
//        // Create a folder for consolidated recordings (if it doesn't exist)
//        createFolder("consolidated/" + stream.getId().toString());

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

        Stream stream = streamRepository.findById(streamDTO.getId()).orElse(null);
        if (stream != null) {
            stream.setStatus("stopped");
            streamRepository.save(stream);
        }

        this.runningTasks.remove(streamDTO.getId());

        return streamDTO;
    }

    public List<StreamDTO> streams() {
        List<Stream> allStreams = streamRepository.findAll();
        return allStreams.stream().map(streamMapper::streamToStreamDTO).collect(Collectors.toList());
    }

    public List<RecordedStreamDTO> getRecordedStreamsByStreamId(Long streamId) {
        List<RecordedStream> recordedStreams = recordStreamRepository.findByStreamId(streamId);
        return recordedStreams.stream().map(streamMapper::recordedStreamToRecordedStreamDTO).collect(Collectors.toList());
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

//    public void consolidateRecordings(Long streamId) {
//        // Define the paths for the existing and consolidated folders
//        String existingFolderPath = Paths.get(AppConfig.getVideosPath(), streamId.toString()).toString();
//        String consolidatedFolderPath = Paths.get(AppConfig.getVideosPath(), "consolidated", streamId.toString()).toString();
//
//        // Create the consolidated folder if it doesn't exist
//        createFolder(consolidatedFolderPath);
//
//        // Get a list of recording files in the existing folder
//        File existingFolder = new File(existingFolderPath);
//
//        if (existingFolder.exists() && existingFolder.isDirectory()) {
//            // Get a list of recording files in the existing folder
//            File[] recordingFiles = existingFolder.listFiles();
//
//            if (recordingFiles != null && recordingFiles.length > 0) {
//                // Create a file list for FFmpeg to concatenate
//                StringBuilder fileList = new StringBuilder();
//                for (File recordingFile : recordingFiles) {
//                    fileList.append("file '").append(recordingFile.getAbsolutePath()).append("'\n");
//                }
//
//                try {
//                    // Create a file containing the list of recording file paths
//                    String fileListPath = Paths.get(consolidatedFolderPath, "file_list.txt").toString();
//                    Files.write(Paths.get(fileListPath), fileList.toString().getBytes());
//
//                    // Use FFmpeg to concatenate the recordings into a single video
//                    String outputVideoPath = Paths.get(consolidatedFolderPath, "consolidated_video.mp4").toString();
//                    ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-f", "concat", "-safe", "0", "-i", fileListPath, "-c:v", "copy", "-c:a", "copy", outputVideoPath);
//                    Process process = processBuilder.start();
//                    process.waitFor();
//
//                    // Delete the file list
//                    Files.delete(Paths.get(fileListPath));
//
//                    // Optionally, you can delete the individual recording files if needed
//                    // for (File recordingFile : recordingFiles) {
//                    //     recordingFile.delete();
//                    // }
//                    System.out.println("Consolidation completed successfully.");
//                } catch (IOException | InterruptedException e) {
//                    System.err.println("Failed to consolidate recordings: " + e.getMessage());
//                }
//            } else {
//                System.err.println("No recording files found in the folder.");
//            }
//        } else {
//            System.err.println("The existing folder does not exist or is not a directory.");
//        }
//    }

//    public void createRecordingFolders(Long streamId) {
//        createFolder(streamId.toString()); // Existing folder for recordings
//        createFolder("consolidated/" + streamId.toString()); // New folder for consolidated recordings
//    }


}
