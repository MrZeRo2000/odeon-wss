package com.romanpulov.odeonwss.service.processor.model;

import java.util.Objects;

public class ProcessingAction {
    private ProcessingActionType actionType;
    private String value;

    public ProcessingActionType getActionType() {
        return actionType;
    }

    public void setActionType(ProcessingActionType actionType) {
        this.actionType = actionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ProcessingAction(ProcessingActionType actionType, String value) {
        this.actionType = actionType;
        this.value = value;
    }

    public ProcessingAction() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingAction that = (ProcessingAction) o;
        return actionType == that.actionType && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, value);
    }

    @Override
    public String toString() {
        return "ProcessingAction{" +
                "actionType=" + actionType +
                ", value='" + value + '\'' +
                '}';
    }
}
