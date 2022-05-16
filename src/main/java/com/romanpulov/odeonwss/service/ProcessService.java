package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final ProcessorFactory processorFactory;

    public ProcessService(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    AtomicReference<AbstractProcessor> currentProcessor = new AtomicReference<>();

    private List<ProgressInfo> progress;

    public List<ProgressInfo> getProgress() {
        return progress;
    }

    synchronized public boolean isRunning() {
        return currentProcessor.get() != null;
    }

    public ProcessingStatus getLastProcessingStatus() {
        if (progress == null) {
            return ProcessingStatus.NOT_RUNNING;
        } else if (progress.size() == 0) {
            return ProcessingStatus.IN_PROGRESS;
        } else {
            return progress.get(progress.size() - 1).getStatus();
        }
    }

    public ProgressInfo getLastProgressInfo() {
        if (progress == null || progress.size() < 2) {
            return null;
        } else {
            return progress.get(progress.size() - 2);
        }
    }

    public ProgressInfo getFinalProgressInfo() {
        if (progress == null || progress.size() == 0) {
            return null;
        } else {
            return progress.get(progress.size() - 1);
        }
    }

    @Override
    public void handleProgress(ProgressInfo progressInfo) {
        progress.add(progressInfo);
    }

    public void executeProcessor(ProcessorType processorType) {
        executeProcessor(processorType, null);
    }

    synchronized public void executeProcessor(ProcessorType processorType, String rootPath) {
        logger.debug("Starting execution: " + processorType + ", parameter path: " + rootPath);

        try {

            if (currentProcessor.get() != null) {
                throw new ProcessorException("Process already running");
            }

            progress = new ArrayList<>();

            currentProcessor.set(processorFactory.fromProcessorType(processorType, this));

            if (rootPath != null) {
                currentProcessor.get().setRootFolder(rootPath);
            }

            // execute
            logger.debug(String.format("Executing: %s with path: %s", processorType, currentProcessor.get().getRootFolder()));
            currentProcessor.get().execute();

            // get task status
            progress.add(ProgressInfo.createTaskStatus(progress));
        } catch (Exception e) {
            progress.add(ProgressInfo.fromException(e));
        } finally {
            currentProcessor.set(null);
        }
    }

    @Async
    public void executeProcessorAsync(ProcessorType processorType) throws Exception {
        executeProcessor(processorType);
    }
}
