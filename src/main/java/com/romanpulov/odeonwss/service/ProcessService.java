package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.repository.ProcessInfoRepository;
import com.romanpulov.odeonwss.service.processor.*;
import com.romanpulov.odeonwss.service.processor.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final ProcessorFactory processorFactory;

    private final ProcessInfoRepository processInfoRepository;

    @Value("${dbprocess.logging}")
    private Boolean dbProcessLogging;

    public Boolean getDbProcessLogging() {
        return dbProcessLogging;
    }

    public void setDbProcessLogging(Boolean dbProcessLogging) {
        this.dbProcessLogging = dbProcessLogging;
    }

    public ProcessService(ProcessorFactory processorFactory, ProcessInfoRepository processInfoRepository) {
        this.processorFactory = processorFactory;
        this.processInfoRepository = processInfoRepository;
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
        processInfo.addProcessDetails(processDetail);
    }

    @Override
    public void handleErrorItem(String message, String item) {
        processInfo.addProgressDetailsErrorItem(message, item);
    }

    @Override
    public void handleProcessingEvent(ProcessingEvent processingEvent) {
        processInfo.setProcessingEvent(processingEvent);
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

                processInfo.addProcessDetails(ProcessDetail.fromInfoMessage(ProcessorMessages.INFO_STARTED, processorType.label));

                if (rootPath != null) {
                    currentProcessor.get().setRootFolder(rootPath);
                }

                // execute
                logger.debug(String.format("Executing: %s with path: %s", processorType, currentProcessor.get().getRootFolder()));
                currentProcessor.get().execute();

            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("Error executing: " + processorType + ": " + e.getMessage());
                processInfo.addProcessDetails(ProcessDetail.fromException(e));
            } finally {
                // final status
                processInfo.finalizeProcess();

                //clean up processor
                currentProcessor.set(null);

                //log process info
                dbLogProcessInfo(processInfo);

                logger.debug("Finished executing: " + processorType);
            }
        } else {
            logger.debug("Another processor is running: " + currentProcessor.get().getClass().getName());
        }
    }

    private void dbLogProcessInfo(ProcessInfo processInfo) {
        if (this.dbProcessLogging) {
            try {
                logger.debug("Saving processing info");
                processInfoRepository.save(processInfo);
                logger.debug("Saved processing info");
            } catch (Exception e) {
                logger.error("Error saving processing info: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Async
    public void executeProcessorAsync(ProcessorType processorType) {
        executeProcessor(processorType);
    }
}
