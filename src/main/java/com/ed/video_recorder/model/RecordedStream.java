package com.ed.video_recorder.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity(name = "RecordedStream")
@Table(name = "recorded_stream")
public class RecordedStream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String fileName;

    @Column
    private String filePath;

    @Column(name = "started_on", updatable = false, nullable = false)
    private Instant startedOn;

    @Column(name = "stopped_on", updatable = false, nullable = false)
    private Instant stoppedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id")
    private Stream stream;
}
