package com.ed.video_recorder.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class StreamDTO {

    private Long id;

    @NotNull
    private String rtspURL;

    private String status;

    @JsonAlias("msg_id")
    private String msgId;

    @JsonAlias("msg_timestamp")
    private String msgTimestamp;

    private String sender;

    private String type;

    private String description;


}
