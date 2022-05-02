package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private Logger logger = LoggerFactory.getLogger(ProcessService.class);

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

    synchronized public void executeProcessor(ProcessorType processorType, String rootPath) throws Exception {
        logger.debug("Starting execution: " + processorType + ", parameter path: " + Optional.ofNullable(rootPath).orElse("null"));

        if (currentProcessor.get() != null) {
            throw new ProcessorException("Process already running");
        }

        progress = new ArrayList<>();

        currentProcessor.set(processorFactory.fromProcessorType(processorType, this));
        try {
            if (rootPath != null) {
                currentProcessor.get().setRootFolder(rootPath);
            }

            // execute
            logger.debug(String.format("Executing: %s with path: %s", processorType, currentProcessor.get().getRootFolder()));
            currentProcessor.get().execute();

            // get task status
            progress.add(ProgressInfo.createTaskStatus(progress));

            // clear processor
            currentProcessor.set(null);
        } catch (Exception e) {
            progress.add(ProgressInfo.fromException(e));
        } finally {
            currentProcessor.set(null);
        }
    }
}
