package com.ed.video_recorder.config;

import com.ed.video_recorder.dto.StreamDTO;
import com.ed.video_recorder.mapper.StreamMapper;
import com.ed.video_recorder.model.Stream;
import com.ed.video_recorder.repository.StreamRepository;
import com.ed.video_recorder.service.VRecService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component()
public class StartUp implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private VRecService vRecService;

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private StreamMapper streamMapper;



    @SneakyThrows
    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        List<Stream> streams = streamRepository.findAll();

        for (Stream stream : streams) {
            StreamDTO streamDTO = streamMapper.streamToStreamDTO(stream);
            vRecService.start(streamDTO);
        }
    }

}
