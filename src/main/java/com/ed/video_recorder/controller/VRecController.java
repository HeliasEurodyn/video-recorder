package com.ed.video_recorder.controller;

import com.ed.video_recorder.dto.StreamDTO;
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
    public StreamDTO start(@RequestBody StreamDTO streamDTO) {
        return vRecService.start(streamDTO);
    }

    @PostMapping("stop")
    public StreamDTO stop(@RequestBody StreamDTO streamDTO) throws ExecutionException, InterruptedException {
        return vRecService.stop(streamDTO);
    }

    @GetMapping("list")
    public List<StreamDTO> findChatMessages() {
        return vRecService.list();
    }
}
