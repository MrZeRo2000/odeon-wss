package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private final ProcessorFactory processorFactory;

    public ProcessService(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    AtomicReference<AbstractProcessor> currentProcessor = new AtomicReference<>();

    private List<ProgressInfo> progress;

    public List<ProgressInfo> getProgress() {
        return progress;
    }

    public ProcessingStatus getLastProcessingStatus() {
        if (progress == null) {
            return ProcessingStatus.NOT_RUNNING;
        } else if (progress.size() == 0) {
            return ProcessingStatus.IN_PROGRESS;
        } else {
            return progress.get(progress.size() - 1).status;
        }
    }

    @Override
    public void handleProgress(ProgressInfo progressInfo) {
        progress.add(progressInfo);
    }

    synchronized public void executeProcessor(ProcessorType processorType, String rootPath) throws Exception {
        if (currentProcessor.get() != null) {
            throw new ProcessorException("Process already running");
        }

        progress = new ArrayList<>();

        currentProcessor.set(processorFactory.fromProcessorType(processorType, this));
        try {
            if (rootPath != null) {
                currentProcessor.get().setRootPath(rootPath);
            }
            currentProcessor.get().execute();
            progress.add(new ProgressInfo("Successfully completed", ProcessingStatus.SUCCESS));
            currentProcessor.set(null);
        } catch (Exception e) {
            progress.add(ProgressInfo.fromException(e));
        } finally {
            currentProcessor.set(null);
        }
    }
}
