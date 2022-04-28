package com.romanpulov.odeonwss.service.processor;

import java.time.LocalDateTime;

public class ProgressInfo {
    public final LocalDateTime time;

    public final String info;

    public final ProcessingStatus status;

    public ProgressInfo(LocalDateTime time, String info, ProcessingStatus status) {
        this.time = time;
        this.info = info;
        this.status = status;
    }
}
