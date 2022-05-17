package com.romanpulov.odeonwss.service.processor.model;

public class ProcessingAction {
    private final ProcessingActionType actionType;

    public ProcessingActionType getActionType() {
        return actionType;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public ProcessingAction(ProcessingActionType actionType, String value) {
        this.actionType = actionType;
        this.value = value;
    }
}
