package com.ed.video_recorder.config;

import com.ed.video_recorder.model.Stream;
import com.ed.video_recorder.repository.StreamRepository;
import com.ed.video_recorder.service.VRecService;
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



    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        List<Stream> streams = streamRepository.findByStatus("running");

        for (Stream stream : streams) {
            vRecService.start(stream);
        }
    }

}
