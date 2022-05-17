package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final ProcessorFactory processorFactory;

    public ProcessService(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    AtomicReference<AbstractProcessor> currentProcessor = new AtomicReference<>();

    private List<ProgressDetail> progressDetails;

    public List<ProgressDetail> getProgressDetails() {
        return progressDetails;
    }

    synchronized public boolean isRunning() {
        return currentProcessor.get() != null;
    }

    public ProcessingStatus getLastProcessingStatus() {
        if (progressDetails == null) {
            return ProcessingStatus.NOT_RUNNING;
        } else if (progressDetails.size() == 0) {
            return ProcessingStatus.IN_PROGRESS;
        } else {
            return progressDetails.get(progressDetails.size() - 1).getStatus();
        }
    }

    public ProgressDetail getLastProgressInfo() {
        if (progressDetails == null || progressDetails.size() < 2) {
            return null;
        } else {
            return progressDetails.get(progressDetails.size() - 2);
        }
    }

    public ProgressDetail getFinalProgressInfo() {
        if (progressDetails == null || progressDetails.size() == 0) {
            return null;
        } else {
            return progressDetails.get(progressDetails.size() - 1);
        }
    }

    @Override
    public void handleProgress(ProgressDetail progressDetail) {
        progressDetails.add(progressDetail);
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

            progressDetails = new ArrayList<>();
            progressDetails.add(ProgressDetail.fromInfoMessage(String.format(ProcessorMessages.INFO_STARTED, processorType.label)));

            currentProcessor.set(processorFactory.fromProcessorType(processorType, this));

            if (rootPath != null) {
                currentProcessor.get().setRootFolder(rootPath);
            }

            // execute
            logger.debug(String.format("Executing: %s with path: %s", processorType, currentProcessor.get().getRootFolder()));
            currentProcessor.get().execute();

            // get task status
            progressDetails.add(ProgressDetail.createTaskStatus(progressDetails));
        } catch (Exception e) {
            progressDetails.add(ProgressDetail.fromException(e));
        } finally {
            currentProcessor.set(null);
        }
    }

    @Async
    public void executeProcessorAsync(ProcessorType processorType) throws Exception {
        executeProcessor(processorType);
    }
}
