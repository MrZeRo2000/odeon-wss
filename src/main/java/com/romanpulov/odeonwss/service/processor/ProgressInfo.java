package com.romanpulov.odeonwss.service.processor;

import java.time.LocalDateTime;

public class ProgressInfo {
    public final LocalDateTime time;

    public final String info;

    public final ProcessingStatus status;

    public ProgressInfo(String info, ProcessingStatus status) {
        this.time = LocalDateTime.now();
        this.info = info;
        this.status = status;
    }

    public static ProgressInfo fromException(Exception e) {
        return new ProgressInfo(e.getMessage(), ProcessingStatus.FAILURE);
    }
}
