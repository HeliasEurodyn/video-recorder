package com.ed.video_recorder.thread;

import com.ed.video_recorder.dto.VideoDataDTO;
import com.ed.video_recorder.repository.VideoRecordRepository;
import lombok.Getter;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

@Getter
public class VideoRecorderThread implements Runnable {

    private final VideoRecordRepository videoRecordRepository;

    private volatile Boolean stopRequested = false;

    private VideoDataDTO videoDataDTO;

    private static final String videosPath = "C:\\Users\\user\\Pictures\\Saved Pictures\\";

    private static final int numFilesToKeep = 10;

    private static final long captureDurationMs = 10000;

//    private static final long captureDurationMs = 10000;

    public VideoRecorderThread(VideoDataDTO videoDataDTO, VideoRecordRepository videoRecordRepository){
        this.videoDataDTO = videoDataDTO;
        this.videoRecordRepository = videoRecordRepository;
        this.createFolder(videoDataDTO.getId());
    }

    public void stop() {
        stopRequested = true;
    }

    @Override
    public void run() {
        while (!stopRequested) {
            this.cleanupFolder();
            this.saveSteam();
        }
    }

    public void saveSteam() {

        OpenCV.loadShared();

        // RTSP URL for your camera
        // String rtspURL = "rtsp://localhost:8554/city-traffic";
        String rtspURL = this.videoDataDTO.getRtspURL();

        // Specify the folder where you want to save the video
        String outputFolder = videosPath + this.videoDataDTO.getId();

        // Create the full path for the output file
        // String outputFileName = Paths.get(outputFolder, "output_video.mp4").toString();
        String outputFileName = Paths.get(outputFolder,  getCurrentDatetime()+  ".mp4").toString();

        // Create a VideoCapture object to open the camera stream
        VideoCapture capture = new VideoCapture(rtspURL);

        if (!capture.isOpened()) {
            System.out.println("Error: Could not open camera stream.mp4");
            return;
        }

        // Define the output file and codec
        int fourcc = VideoWriter.fourcc('H','2','6','4');

        // Create a VideoWriter to save the video
        VideoWriter writer = new VideoWriter(outputFileName, fourcc, 20.0, new org.opencv.core.Size(1920, 1080), true);

        if (!writer.isOpened()) {
            System.out.println("Error: Could not open VideoWriter.");
            return;
        }

        // Capture frames for 10 minutes
        long startTime = System.currentTimeMillis();

        Mat frame = new Mat();
        while (System.currentTimeMillis() - startTime < captureDurationMs) {
            if (capture.read(frame)) {
                writer.write(frame);
            } else {
                System.out.println("Error: Frame not read.");
                break;
            }
        }

        capture.release();
        writer.release();

        System.out.println("Video capture completed.");
    }

    public void createFolder(String folderName) {

        // Create a Path object representing the directory
        Path folder = Paths.get(videosPath + folderName);

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

    public void cleanupFolder() {

        File folder = new File(videosPath + this.videoDataDTO.getId());

        if (folder.exists() && folder.isDirectory()) {

            File[] files = folder.listFiles();

            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    long file1CreationTime = file1.lastModified();
                    long file2CreationTime = file2.lastModified();
                    return Long.compare(file1CreationTime, file2CreationTime);
                }
            });

            // Delete all files except the last N
            int numFilesToDelete = files.length - numFilesToKeep;
            for (int i = 0; i < numFilesToDelete; i++) {
                File fileToDelete = files[i];
                if (fileToDelete.delete()) {
                    System.out.println("Deleted file: " + fileToDelete.getName());
                } else {
                    System.err.println("Failed to delete file: " + fileToDelete.getName());
                }
            }
        } else {
            System.out.println("The specified folder does not exist or is not a directory.");
        }
    }

    public String getCurrentDatetime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define a custom date and time format for the filename
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Format the current date and time as a string
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

}
