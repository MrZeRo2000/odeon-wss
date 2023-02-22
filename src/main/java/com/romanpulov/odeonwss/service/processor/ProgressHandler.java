package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;

public interface ProgressHandler {
    void handleProgress(ProcessDetail processDetail);
    void handleErrorItem(String message, String item);
}
