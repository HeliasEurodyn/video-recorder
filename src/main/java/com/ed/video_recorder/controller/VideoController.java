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

    @CrossOrigin(origins = {"http://localhost:4200", "http://localhost:50574"})
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

}
