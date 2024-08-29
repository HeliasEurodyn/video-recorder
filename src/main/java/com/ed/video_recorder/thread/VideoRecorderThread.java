package com.ed.video_recorder.thread;

import com.ed.video_recorder.model.RecordedStream;
import com.ed.video_recorder.model.Stream;
import com.ed.video_recorder.repository.RecordStreamRepository;
import com.ed.video_recorder.repository.StreamRepository;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import com.ed.video_recorder.config.AppConfig;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class VideoRecorderThread implements Runnable {

    private final RecordStreamRepository recordStreamRepository;
    private final StreamRepository streamRepository;
    private volatile Boolean stopRequested = false;
    private volatile Boolean recordingEnded = false;
    private final Stream stream;

    private VideoCapture capture;
    private VideoWriter writer;



    public VideoRecorderThread(Stream stream, RecordStreamRepository recordStreamRepository, StreamRepository streamRepository) {
        this.stream = stream;
        this.recordStreamRepository = recordStreamRepository;
        this.streamRepository = streamRepository;
    }

    public void stop() {
        stopRequested = true;
        recordingEnded = true;
    }

    @Override
    public void run() {
        while (!stopRequested) {
            this.cleanupFolder();
            this.saveRecordedStreamToFs();

        }
    }

    public void saveRecordedStreamToFs() {

        OpenCV.loadShared();

        // RTSP URL for your camera
        // String rtspURL = "rtsp://localhost:8554/city-traffic";
        String url = this.stream.getRtspURL();

        // Specify the folder where you want to save the video
        String outputFolder = AppConfig.getVideosPath() + this.stream.getId();

        // Create the full path for the output file
        // String outputFileName = Paths.get(outputFolder, "output_video.mp4").toString();
        String filename = getCurrentDatetime() + ".mp4";
        String outputFileName = Paths.get(outputFolder, filename).toString();
//        String generatedFilename = filename;
//        videoFileCounter++;

        // Create a VideoCapture object to open the camera stream
        VideoCapture capture = new VideoCapture(url);

        if (!capture.isOpened()) {
            System.out.println("Error: Could not open camera stream.mp4");
            return;
        }

        // Define the output file and codec
        int fourcc = VideoWriter.fourcc('H', '2', '6', '4');

        // Create a VideoWriter to save the video
        VideoWriter writer = new VideoWriter(outputFileName, fourcc, 20.0, new org.opencv.core.Size(1920, 1080), true);

        if (!writer.isOpened()) {
            System.out.println("Error: Could not open VideoWriter.");
            return;
        }

        // Capture frames for 10 minutes
        long startTime = System.currentTimeMillis();
        Instant startTimeInstant = Instant.ofEpochMilli(startTime);
        Mat frame = new Mat();
        while (System.currentTimeMillis() - startTime < AppConfig.getCaptureDurationMs()) {
            if (capture.read(frame)) {
                writer.write(frame);
            } else {
                System.out.println("Error: Frame not read.");
                break;
            }
        }

        capture.release();
        writer.release();

        saveRecordedStreamToDb(outputFolder, filename, startTimeInstant, startTimeInstant.plusMillis(AppConfig.getCaptureDurationMs()));

        System.out.println("Video capture completed.");
    }

//    public void saveRecordedStreamToFs() {
//
//        OpenCV.loadShared();
//
//        String url = this.stream.getRtspURL();
//
//        String outputFolder = AppConfig.getVideosPath() + this.stream.getId();
//
//        boolean writerOpened = false;
//
//        String filename = getCurrentDatetime() + ".mp4";
//        String outputFileName = Paths.get(outputFolder, filename).toString();
//
//        VideoCapture capture = new VideoCapture(url);
//
//        // Capture frames for a specified duration
//        long startTime = System.currentTimeMillis();
//        Instant startTimeInstant = Instant.ofEpochMilli(startTime);
//        Mat frame = new Mat();
//
//        while (System.currentTimeMillis() - startTime < AppConfig.getCaptureDurationMs()) {
//            if (writer == null || !writerOpened) {
//                // Create a new VideoWriter when not initialized or not opened
//                writer = createVideoWriter(outputFileName);
//                writerOpened = writer.isOpened();
//            }
//
//            if (capture.read(frame)) {
//                writer.write(frame);
//            } else {
//                System.out.println("Error: Frame not read.");
//                break;
//            }
//        }
//
//        if (writer != null && writer.isOpened()) {
//            writer.release();
//        }
//
//        capture.release();
//
//        saveRecordedStreamToDb(outputFolder, filename, startTimeInstant, startTimeInstant.plusMillis(AppConfig.getCaptureDurationMs()));
//
//        System.out.println("Video capture completed.");
//    }
//
//    private VideoWriter createVideoWriter(String outputFileName) {
//        int fourcc = VideoWriter.fourcc('H', '2', '6', '4');
//        return new VideoWriter(outputFileName, fourcc, 20.0, new org.opencv.core.Size(1920, 1080), true);
//    }

//    public void saveRecordedStreamToFs() {
//        OpenCV.loadShared();
//
//        String url = this.stream.getRtspURL();
//        String outputFolder = AppConfig.getVideosPath() + this.stream.getId();
//        Mat frame = new Mat();
//
//        VideoWriter firstWriter = null;
//        VideoWriter secondWriter = null;
//        boolean firstWriterOpened = false;
//        boolean secondWriterOpened = false;
//        long startTime = System.currentTimeMillis();
//
//        long captureDurationMs = AppConfig.getCaptureDurationMs(); // Adjust this as needed
//        long switchTimeMs = 5000; // 5 seconds before switching to the second writer
//
//        while (System.currentTimeMillis() - startTime < captureDurationMs) {
//            long currentTime = System.currentTimeMillis();
//
//            // Check if it's time to switch to the second writer
//            if (firstWriter != null && currentTime >= switchTimeMs && !secondWriterOpened) {
//                if (firstWriterOpened) {
//                    firstWriter.release(); // Release the first writer
//                    firstWriterOpened = false;
//                }
//
//                // Create the second writer
//                String filename = getCurrentDatetime() + "_second.mp4";
//                String outputFileName = Paths.get(outputFolder, filename).toString();
//                secondWriter = createVideoWriter(outputFileName);
//                secondWriterOpened = secondWriter.isOpened();
//            }
//
//            // If neither writer is open, create the first writer
//            if (firstWriter == null || !firstWriterOpened) {
//                String filename = getCurrentDatetime() + ".mp4";
//                String outputFileName = Paths.get(outputFolder, filename).toString();
//                firstWriter = createVideoWriter(outputFileName);
//                firstWriterOpened = firstWriter.isOpened();
//            }
//
//            if (firstWriterOpened && firstWriter.isOpened()) {
//                if (captureDurationMs > 0) {
//                    if (currentTime >= switchTimeMs + captureDurationMs) {
//                        // Release the first writer after its predefined duration
//                        firstWriter.release();
//                        firstWriterOpened = false;
//                    }
//                }
//
//                if (capture.read(frame)) {
//                    firstWriter.write(frame);
//                } else {
//                    System.out.println("Error: Frame not read.");
//                    break;
//                }
//            }
//
//            // Continue capturing frames with the second writer
//            if (secondWriterOpened && secondWriter.isOpened()) {
//                if (capture.read(frame)) {
//                    secondWriter.write(frame);
//                } else {
//                    System.out.println("Error: Frame not read.");
//                    break;
//                }
//            }
//        }
//
//        if (firstWriter != null && firstWriter.isOpened()) {
//            firstWriter.release();
//        }
//
//        if (secondWriter != null && secondWriter.isOpened()) {
//            secondWriter.release();
//        }
//
//        capture.release();
//        System.out.println("Video capture completed.");
//    }
//
//    private VideoWriter createVideoWriter(String outputFileName) {
//        int fourcc = VideoWriter.fourcc('H', '2', '6', '4');
//        return new VideoWriter(outputFileName, fourcc, 20.0, new org.opencv.core.Size(1920, 1080), true);
//    }


    public void cleanupFolder() {

        File folder = new File(AppConfig.getVideosPath() + this.stream.getId());

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
            int numFilesToDelete = files.length - AppConfig.getMaxRecordCount();
            for (int i = 0; i < numFilesToDelete; i++) {
                File fileToDelete = files[i];
                if (fileToDelete.delete()) {
                    recordStreamRepository.deleteByFileNameEqualsAndStreamIdEquals(fileToDelete.getName(), this.stream.getId());
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

    private void saveRecordedStreamToDb(String filePath, String fileName, Instant startedOn, Instant stopedOn) {
        RecordedStream recordedStream = new RecordedStream();


        // Set the file path and name
        recordedStream.setFilePath(filePath);
        recordedStream.setFileName(fileName);
        recordedStream.setStartedOn(startedOn);
        recordedStream.setStoppedOn(stopedOn);

        Stream stream = streamRepository.findById(this.stream.getId()).orElseThrow(() -> new EntityNotFoundException("Stream not found with id: " + this.stream.getId()));

        recordedStream.setStream(stream);

        // Save the RecordedStream entity to the database
        recordStreamRepository.save(recordedStream);

    }

}
