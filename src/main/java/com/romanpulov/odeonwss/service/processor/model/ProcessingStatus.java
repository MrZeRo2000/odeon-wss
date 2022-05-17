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

    public final String label;

    ProcessingStatus(String label) {
        this.label = label;
    }
}
