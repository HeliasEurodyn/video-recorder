package com.ed.video_recorder.controller;

import com.ed.video_recorder.dto.RecordedStreamDTO;
import com.ed.video_recorder.dto.StreamDTO;
import com.ed.video_recorder.service.VRecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Slf4j
@Validated
@RestController
@RequestMapping("/video-recorder")
public class VRecController {

    private final VRecService vRecService;

    public VRecController(VRecService vRecService) {
        this.vRecService = vRecService;
    }

    @CrossOrigin(origins = {"*"})
    @Validated
    @PostMapping("start")
    public StreamDTO start(@Valid @RequestBody StreamDTO streamDTO) {
        return vRecService.saveAndStart(streamDTO);
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("stop")
    public StreamDTO stop(@RequestBody StreamDTO streamDTO) throws ExecutionException, InterruptedException {
        return vRecService.stop(streamDTO);
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping(value = "list-streams", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StreamDTO> streams() {
        return vRecService.streams();
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(value = "list-recordedStreams/{streamId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RecordedStreamDTO> recordedStreams(@PathVariable Long streamId) {
        return vRecService.getRecordedStreamsByStreamId(streamId);
    }

    @CrossOrigin(origins = {"*"})
    @DeleteMapping("delete/{id}")
    public void deleteStream(@PathVariable Long id) {
      vRecService.deleteStream(id);
    }

}

