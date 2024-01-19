package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DBProcessInfoDTO extends BaseEntityDTO {
    ProcessorType getProcessorType();
    ProcessingStatus getProcessingStatus();
    LocalDateTime getUpdateDateTime();
}
