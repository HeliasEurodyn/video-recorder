package com.ed.video_recorder.thread;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Future;

@Data
@AllArgsConstructor
public class ThreadTupple {

    private VideoRecorderThread videoRecorderThread;

    private Future<?> future;

}
