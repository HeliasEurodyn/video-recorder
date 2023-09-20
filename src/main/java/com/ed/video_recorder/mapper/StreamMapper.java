package com.ed.video_recorder.mapper;

import com.ed.video_recorder.dto.RecordedStreamDTO;
import com.ed.video_recorder.dto.StreamDTO;
import com.ed.video_recorder.model.RecordedStream;
import com.ed.video_recorder.model.Stream;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StreamMapper {


    StreamDTO streamToStreamDTO(Stream stream);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "rtspURL", source = "rtspURL")
    Stream streamDTOToStream(StreamDTO streamDTO);


    RecordedStream recordStreamDTOtoRecordStream(RecordedStreamDTO recordedStreamDTO);


}
