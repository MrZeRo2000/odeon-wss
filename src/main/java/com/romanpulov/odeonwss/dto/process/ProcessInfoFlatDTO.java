package com.romanpulov.odeonwss.dto.process;

import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import java.time.LocalDateTime;

public interface ProcessInfoFlatDTO {
    Long getId();
    ProcessorType getProcessorType();
    ProcessingStatus getProcessingStatus();
    LocalDateTime getUpdateDateTime();
    // detail
    LocalDateTime getDetailUpdateDateTime();
    ProcessingStatus getDetailStatus();
    String getDetailMessage();
    Integer getDetailRows();
    // items
    String getDetailItem();
    // action
    ProcessingActionType getProcessingActionType();
    String getProcessingActionValue();
}
