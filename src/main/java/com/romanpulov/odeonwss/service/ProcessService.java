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

    final AtomicReference<AbstractProcessor> currentProcessor = new AtomicReference<>();

    private ProcessInfo processInfo;

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public void clearProcessInfo() {
        this.processInfo = null;
    }

    @Override
    public void handleProgress(ProcessDetail processDetail) {
        processInfo.addProgressDetails(processDetail);
    }

    @Override
    public void handleErrorItem(String message, String item) {
        processInfo.addProgressDetailsErrorItem(message, item);
    }

    public void executeProcessor(ProcessorType processorType) {
        executeProcessor(processorType, null);
    }

    synchronized public void executeProcessor(ProcessorType processorType, String rootPath) {
        logger.debug("Starting execution: " + processorType + ", parameter path: " + rootPath);

        if (currentProcessor.get() == null) {
            processInfo = new ProcessInfo(processorType);
            try {
                currentProcessor.set(processorFactory.fromProcessorType(processorType, this));

                processInfo.addProgressDetails(ProcessDetail.fromInfoMessage(ProcessorMessages.INFO_STARTED, processorType.label));

                if (rootPath != null) {
                    currentProcessor.get().setRootFolder(rootPath);
                }

                // execute
                logger.debug(String.format("Executing: %s with path: %s", processorType, currentProcessor.get().getRootFolder()));
                currentProcessor.get().execute();

            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("Error executing: " + processorType + ": " + e.getMessage());
                processInfo.addProgressDetails(ProcessDetail.fromException(e));
            } finally {
                // final status
                ProcessDetail finalProcessDetail = ProcessDetail.createFinalProgressDetail(processInfo.getProgressDetails());
                processInfo.addProgressDetails(finalProcessDetail);
                processInfo.setProcessingStatus(finalProcessDetail.getStatus());

                //clean up processor
                currentProcessor.set(null);

                logger.debug("Finished executing: " + processorType);
            }
        } else {
            logger.debug("Another processor is running: " + currentProcessor.get().getClass().getName());
        }
    }

    @Async
    public void executeProcessorAsync(ProcessorType processorType) {
        executeProcessor(processorType);
    }
}
