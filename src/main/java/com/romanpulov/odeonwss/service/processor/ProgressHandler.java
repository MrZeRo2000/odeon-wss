package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;

public interface ProgressHandler {
    void handleProgress(ProgressDetail progressDetail);
    void handleErrorItem(String message, String item);
}
