package com.ed.video_recorder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class RecordedStreamDTO {

    private Long id;

    private String fileName;

    private String filePath;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant startedOn;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant stoppedOn;


}
