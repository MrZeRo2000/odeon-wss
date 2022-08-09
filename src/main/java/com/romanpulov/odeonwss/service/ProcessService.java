package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.service.processor.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final ProcessorFactory processorFactory;

    public ProcessService(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    AtomicReference<AbstractProcessor> currentProcessor = new AtomicReference<>();

    private ProcessInfo processInfo;

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public void clearProcessInfo() {
        this.processInfo = null;
    }

    @Override
    public void handleProgress(ProgressDetail progressDetail) {
        processInfo.addProgressDetails(progressDetail);
    }

    public void executeProcessor(ProcessorType processorType) {
        executeProcessor(processorType, null);
    }

    synchronized public void executeProcessor(ProcessorType processorType, String rootPath) {
        logger.debug("Starting execution: " + processorType + ", parameter path: " + rootPath);

        processInfo = new ProcessInfo(processorType);
        try {

            if (currentProcessor.get() != null) {
                throw new ProcessorException("Process already running");
            }

            currentProcessor.set(processorFactory.fromProcessorType(processorType, this));

            processInfo.addProgressDetails(ProgressDetail.fromInfoMessage(ProcessorMessages.INFO_STARTED, processorType.label));

            if (rootPath != null) {
                currentProcessor.get().setRootFolder(rootPath);
            }

            // execute
            logger.debug(String.format("Executing: %s with path: %s", processorType, currentProcessor.get().getRootFolder()));
            currentProcessor.get().execute();

        } catch (Exception e) {
            e.printStackTrace();
            processInfo.addProgressDetails(ProgressDetail.fromException(e));
        } finally {
            // final status
            ProgressDetail finalProgressDetail = ProgressDetail.createFinalProgressDetail(processInfo.getProgressDetails());
            processInfo.addProgressDetails(finalProgressDetail);
            processInfo.setProcessingStatus(finalProgressDetail.getStatus());

            //clean up processor
            currentProcessor.set(null);
        }
    }

    @Async
    public void executeProcessorAsync(ProcessorType processorType) {
        executeProcessor(processorType);
    }
}
