package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessService implements ProgressHandler {
    private AbstractProcessor processor;

    List<ProgressInfo> progress;

    public List<ProgressInfo> getProgress() {
        return progress;
    }

    @Override
    public void handleProgress(ProgressInfo progressInfo) {
        progress.add(progressInfo);
    }

    void executeProcessor(ProcessorType processorType) {
        AbstractProcessor processor = ProcessorFactory.fromProcessorType(processorType, this);
        processor.execute();
    }
}
