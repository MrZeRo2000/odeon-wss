package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;

public interface ProgressHandler {
    void handleProgress(ProgressDetail progressDetail);
}
