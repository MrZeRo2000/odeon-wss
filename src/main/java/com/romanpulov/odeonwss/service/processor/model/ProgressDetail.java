package com.romanpulov.odeonwss.service.processor.model;

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
        return fromErrorMessage(e.getMessage() == null ? "Unsupported operation" : e.getMessage());
    }

    public static ProgressDetail fromInfoMessage(String errorMessage) {
        return new ProgressDetail(errorMessage, ProcessingStatus.INFO);
    }

    public static ProgressDetail fromInfoMessage(String errorMessage, Object ...args) {
        return fromInfoMessage(String.format(errorMessage, args));
    }

    public static ProgressDetail fromErrorMessage(String errorMessage) {
        return new ProgressDetail(errorMessage, ProcessingStatus.FAILURE);
    }

    public static ProgressDetail fromErrorMessage(String errorMessage, Object ...args) {
        return fromErrorMessage(String.format(errorMessage, args));
    }

    public static ProgressDetail fromWarningMessage(String warningMessage) {
        return new ProgressDetail(warningMessage, ProcessingStatus.WARNING);
    }

    public static ProgressDetail fromWarningMessage(String warningMessage, Object ...args) {
        return fromWarningMessage(String.format(warningMessage, args));
    }

    public static ProgressDetail fromWarningMessageWithAction(String warningMessage, ProcessingActionType processingActionType, String actionValue) {
        return new ProgressDetail(warningMessage, ProcessingStatus.WARNING, new ProcessingAction(processingActionType, actionValue));
    }

    public static ProcessingStatus getFinalProcessingStatus(List<ProgressDetail> progressDetails) {
        List<ProcessingStatus> processingStatuses = progressDetails.stream().map(p -> p.status).distinct().collect(Collectors.toList());
        if (processingStatuses.contains(ProcessingStatus.FAILURE)) {
            return ProcessingStatus.FAILURE;
        } else if (processingStatuses.contains(ProcessingStatus.WARNING)) {
            return ProcessingStatus.WARNING;
        } else {
            return ProcessingStatus.SUCCESS;
        }
    }

    public static ProgressDetail createFinalProgressDetail(List<ProgressDetail> progressDetails) {
        return new ProgressDetail("Task status", getFinalProcessingStatus(progressDetails));
    }

    @Override
    public String toString() {
        return "ProgressDetail{" +
                "time=" + time +
                ", info='" + info + '\'' +
                ", status=" + status +
                ", processingAction=" + processingAction +
                '}';
    }
}
