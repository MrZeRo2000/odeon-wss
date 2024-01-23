package com.romanpulov.odeonwss.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.BaseEntityDTO;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ProcessInfoDTO extends BaseEntityDTO {
    ProcessorType getProcessorType();
    ProcessingStatus getProcessingStatus();
    LocalDateTime getUpdateDateTime();
    List<ProcessDetailDTO> getProcessDetails();
    ProcessingEventDTO getProcessingEvent();
}
