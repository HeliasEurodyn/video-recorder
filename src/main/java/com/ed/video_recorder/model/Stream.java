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


}
