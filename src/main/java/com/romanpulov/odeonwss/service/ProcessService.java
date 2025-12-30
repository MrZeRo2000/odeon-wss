package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.dto.process.ProcessInfoDTO;
import com.romanpulov.odeonwss.dto.process.ProcessInfoFlatDTO;
import com.romanpulov.odeonwss.dto.process.ProcessInfoTransformer;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.repository.DBProcessInfoRepository;
import com.romanpulov.odeonwss.repository.ProcessInfoRepository;
import com.romanpulov.odeonwss.service.processor.AbstractProcessor;
import com.romanpulov.odeonwss.service.processor.ProcessorFactory;
import com.romanpulov.odeonwss.service.processor.ProcessorMessages;
import com.romanpulov.odeonwss.service.processor.ProgressHandler;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;
import com.romanpulov.odeonwss.service.processor.model.ProcessInfo;
import com.romanpulov.odeonwss.service.processor.model.ProcessingEvent;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProcessService implements ProgressHandler {

    private final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final ProcessorFactory processorFactory;
    private final ProcessInfoRepository processInfoRepository;
    private final DBProcessInfoRepository dbProcessInfoRepository;
    private final ProcessInfoTransformer processInfoTransformer;

    @Value("${dbprocess.logging}")
    private Boolean dbProcessLogging;

    public Boolean getDbProcessLogging() {
        return dbProcessLogging;
    }

    public void setDbProcessLogging(Boolean dbProcessLogging) {
        this.dbProcessLogging = dbProcessLogging;
    }

    public ProcessService(
            ProcessorFactory processorFactory,
            ProcessInfoRepository processInfoRepository,
            DBProcessInfoRepository dbProcessInfoRepository,
            ProcessInfoTransformer processInfoTransformer) {
        this.processorFactory = processorFactory;
        this.processInfoRepository = processInfoRepository;
        this.dbProcessInfoRepository = dbProcessInfoRepository;
        this.processInfoTransformer = processInfoTransformer;
    }

    final AtomicReference<AbstractProcessor> currentProcessor = new AtomicReference<>();

    private ProcessInfo processInfo;

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public ProcessInfoDTO getProcessInfoDTO() {
        return processInfo == null ? null : processInfoTransformer.transform(processInfo);
    }

    @Transactional(readOnly = true)
    public ProcessInfoDTO getById(Long id) throws CommonEntityNotFoundException {
        List<ProcessInfoFlatDTO> flatDTOS = dbProcessInfoRepository.findFlatDTOByIdWithDetails(id);
        if (flatDTOS.isEmpty()) {
            throw new CommonEntityNotFoundException("DBProcessInfo", id);
        } else {
            return processInfoTransformer.transform(flatDTOS);
        }
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
                logger.debug("Error executing: " + processorType, e);
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
                logger.debug("Error saving processing info", e);
            }
        }
    }

    @Async
    public void executeProcessorAsync(ProcessorType processorType) {
        executeProcessor(processorType);
    }
}
