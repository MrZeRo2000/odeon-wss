package com.romanpulov.odeonwss.dto.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingActionDTO {
    private ProcessingActionType actionType;

    public ProcessingActionType getActionType() {
        return actionType;
    }

    private String value;

    public String getValue() {
        return value;
    }

    private ProcessingActionDTO() {}

    public static ProcessingActionDTO from(ProcessingActionType actionType, String value) {
        ProcessingActionDTO instance = new ProcessingActionDTO();
        instance.actionType = actionType;
        instance.value = value;
        return instance;
    }
}
