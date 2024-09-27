package com.ed.video_recorder.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Stream")
@Table(name = "stream")
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String rtspURL;

    @Column
    private String status;

    @Column( name = "msg_id")
    private String msgId;

    @Column( name = "msg_timestamp")
    private String msgTimestamp;

    @Column
    private String sender;

    @Column
    private String type;

    @Column
    private String description;


}
