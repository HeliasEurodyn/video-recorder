package com.ed.video_recorder.service;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;

@Service
public class StreamCheckService {

//    static {
//        // Load the OpenCV native library
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }

    public boolean isStreamAlive(String rtspUrl) {
        VideoCapture capture = new VideoCapture();
        boolean isAlive = false;
        try {
            // Open the RTSP stream
            capture.open(rtspUrl);
            if (!capture.isOpened()) {
                return false; // Could not open the stream
            }

            // Check if we can grab a frame
            Mat frame = new Mat();
            isAlive = capture.read(frame);
            frame.release();
        } catch (Exception e) {
            // Log the exception with a proper logging framework
            System.err.println("Exception occurred while checking the stream: " + e.getMessage());
        } finally {
            // Release the capture
            capture.release();
        }
        return isAlive; // Return true if a frame was successfully read
    }
}
