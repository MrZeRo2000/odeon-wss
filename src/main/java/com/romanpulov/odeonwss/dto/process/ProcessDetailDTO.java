package com.romanpulov.odeonwss.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.dto.BaseEntityDTO;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ProcessDetailDTO extends BaseEntityDTO {
    LocalDateTime getUpdateDateTime();
    ProcessingStatus getStatus();
    String getMessage();
    Long getRows();
    List<String> getItems();
    ProcessingActionDTO getProcessingAction();
}
