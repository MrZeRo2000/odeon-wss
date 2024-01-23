package com.romanpulov.odeonwss.dto.process;

import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;

public class ProcessingActionDTOImpl implements ProcessingActionDTO {
    private ProcessingActionType actionType;

    @Override
    public ProcessingActionType getActionType() {
        return actionType;
    }

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    private ProcessingActionDTOImpl() {}

    public static ProcessingActionDTOImpl from(ProcessingActionType actionType, String value) {
        ProcessingActionDTOImpl instance = new ProcessingActionDTOImpl();
        instance.actionType = actionType;
        instance.value = value;
        return instance;
    }
}
