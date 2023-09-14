package com.ed.video_recorder.controller;

import com.ed.video_recorder.dto.VideoDataDTO;
import com.ed.video_recorder.service.VRecService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/video-recorder")
public class VRecController {

    private final VRecService vRecService;

    public VRecController(VRecService vRecService) {
        this.vRecService = vRecService;
    }

    @PostMapping("start")
    public VideoDataDTO start(@RequestBody VideoDataDTO videoDataDTO) {
        return vRecService.start(videoDataDTO);
    }

    @PostMapping("stop")
    public VideoDataDTO stop(@RequestBody VideoDataDTO videoDataDTO) throws ExecutionException, InterruptedException {
        return vRecService.stop(videoDataDTO);
    }

    @GetMapping("list")
    public List<VideoDataDTO> findChatMessages() {
        return vRecService.list();
    }
}
