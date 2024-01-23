package com.romanpulov.odeonwss.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ProcessingActionDTO {
    ProcessingActionType getActionType();
    String getValue();
}
