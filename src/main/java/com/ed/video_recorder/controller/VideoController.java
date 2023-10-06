package com.ed.video_recorder.controller;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import lombok.extern.log4j.Log4j2;
import org.opencv.videoio.VideoWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



import java.io.File;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/video")
@Log4j2
public class VideoController {

//    @GetMapping(value = "/live.mp4")
//    @ResponseBody
//    public ResponseEntity<StreamingResponseBody> livestream() throws Exception {
//
//        String videoFilePath = "C:\\Users\\kaftz\\Fake-RTSP-Stream-main\\video-recording\\173\\20230928111933.mp4";
//
//
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(os -> {
//                    FFmpeg.atPath()
//                            .addArgument("-re")
//                            .addArguments("-i", videoFilePath)  // Set the input file path
//                            .addArguments("-c:v", "copy")  // Copy video codec
//                            .addArguments("-c:a", "aac")  // AAC audio codec
//                            .addArguments("-b:a", "96k")  // Audio bitrate
//                            .addArguments("-f", "ismv")   // Output format
//                            .addOutput(PipeOutput.pumpTo(os)
//                                    .disableStream(StreamType.AUDIO)
//                                    .disableStream(StreamType.SUBTITLE)
//                                    .disableStream(StreamType.DATA)
//                                    .setFrameCount(StreamType.VIDEO, 100L)
//                                    // 1 frame every 10 seconds
//                                    .setFrameRate(0.1)
//                                    .setDuration(1, TimeUnit.HOURS))
//                            .addArgument("-nostdin")
//                            .execute();
//                });
//    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/live2.mp4/{streamId}/{fileName:.+}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String streamId, @PathVariable String fileName) {
        String videoFolderPath = "C:\\Users\\kaftz\\Fake-RTSP-Stream-main\\video-recording\\" + streamId + "\\" + fileName;
        File videoFile = new File(videoFolderPath);

        if (videoFile.exists()) {
            Resource videoResource = new FileSystemResource(videoFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .body(videoResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @CrossOrigin(origins = "http://localhost:4200")
//    @GetMapping("/video/{fileName:.+}")
//    public ResponseEntity<Resource> streamVideo(@PathVariable String fileName) {
//        String videoFolderPath = "C:\\Users\\kaftz\\Fake-RTSP-Stream-main\\video-recording\\175\\" + fileName;
//        File videoFile = new File(videoFolderPath);
//
//        if (videoFile.exists()) {
//            Resource videoResource = new FileSystemResource(videoFile);
//            return ResponseEntity.ok()
//                    .contentType(MediaType.valueOf("video/mp4"))
//                    .body(videoResource);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
