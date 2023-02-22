package com.romanpulov.odeonwss.service.processor.model;

/**
 * Processing status for load tasks
 */
public enum ProcessingStatus {
    IN_PROGRESS("In Progress"),
    SUCCESS("Success"),
    INFO("Info"),
    FAILURE("Failure"),
    WARNING("Warning"),
    NOT_RUNNING("Not Running");

    private final String label;

    public String getLabel() {
        return label;
    }

    ProcessingStatus(String label) {
        this.label = label;
    }
}
