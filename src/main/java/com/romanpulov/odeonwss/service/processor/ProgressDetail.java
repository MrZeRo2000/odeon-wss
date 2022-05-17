package com.romanpulov.odeonwss.service.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProgressDetail {
    private final LocalDateTime time;

    public LocalDateTime getTime() {
        return time;
    }

    private final String info;

    public String getInfo() {
        return info;
    }

    private final ProcessingStatus status;

    public ProcessingStatus getStatus() {
        return status;
    }

    private final ProcessingAction processingAction;

    public ProcessingAction getProcessingAction() {
        return processingAction;
    }

    public ProgressDetail(String info, ProcessingStatus status) {
        this(info, status, null);
    }

    public ProgressDetail(String info, ProcessingStatus status, ProcessingAction processingAction) {
        this.time = LocalDateTime.now();
        this.info = info;
        this.status = status;
        this.processingAction = processingAction;
    }

    public static ProgressDetail fromException(Exception e) {
        return fromErrorMessage(e.getMessage());
    }

    public static ProgressDetail fromInfoMessage(String errorMessage) {
        return new ProgressDetail(errorMessage, ProcessingStatus.INFO);
    }

    public static ProgressDetail fromErrorMessage(String errorMessage) {
        return new ProgressDetail(errorMessage, ProcessingStatus.FAILURE);
    }

    public static ProgressDetail fromWarningMessage(String warningMessage) {
        return new ProgressDetail(warningMessage, ProcessingStatus.WARNING);
    }

    public static ProgressDetail fromWarningMessageWithAction(String warningMessage, ProcessingActionType processingActionType, String actionValue) {
        return new ProgressDetail(warningMessage, ProcessingStatus.WARNING, new ProcessingAction(processingActionType, actionValue));
    }

    public static ProcessingStatus getProcessingStatus(List<ProgressDetail> progressDetails) {
        List<ProcessingStatus> processingStatuses = progressDetails.stream().map(p -> p.status).distinct().collect(Collectors.toList());
        if (processingStatuses.contains(ProcessingStatus.FAILURE)) {
            return ProcessingStatus.FAILURE;
        } else if (processingStatuses.contains(ProcessingStatus.WARNING)) {
            return ProcessingStatus.WARNING;
        } else {
            return ProcessingStatus.SUCCESS;
        }
    }

    public static ProgressDetail createTaskStatus(List<ProgressDetail> progressDetails) {
        return new ProgressDetail("Task status", getProcessingStatus(progressDetails));
    }
}
