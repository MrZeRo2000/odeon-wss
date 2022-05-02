package com.romanpulov.odeonwss.service.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProgressInfo {
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

    public ProgressInfo(String info, ProcessingStatus status) {
        this(info, status, null);
    }

    public ProgressInfo(String info, ProcessingStatus status, ProcessingAction processingAction) {
        this.time = LocalDateTime.now();
        this.info = info;
        this.status = status;
        this.processingAction = processingAction;
    }

    public static ProgressInfo fromException(Exception e) {
        return fromErrorMessage(e.getMessage());
    }

    public static ProgressInfo fromErrorMessage(String errorMessage) {
        return new ProgressInfo(errorMessage, ProcessingStatus.FAILURE);
    }

    public static ProgressInfo fromWarningMessage(String warningMessage) {
        return new ProgressInfo(warningMessage, ProcessingStatus.WARNING);
    }

    public static ProgressInfo fromWarningMessageWithAction(String warningMessage, ProcessingActionType processingActionType, String actionValue) {
        return new ProgressInfo(warningMessage, ProcessingStatus.WARNING, new ProcessingAction(processingActionType, actionValue));
    }

    public static ProcessingStatus getProcessingStatus(List<ProgressInfo> progressInfos) {
        List<ProcessingStatus> processingStatuses = progressInfos.stream().map(p -> p.status).distinct().collect(Collectors.toList());
        if (processingStatuses.contains(ProcessingStatus.FAILURE)) {
            return ProcessingStatus.FAILURE;
        } else if (processingStatuses.contains(ProcessingStatus.WARNING)) {
            return ProcessingStatus.WARNING;
        } else {
            return ProcessingStatus.SUCCESS;
        }
    }

    public static ProgressInfo createTaskStatus(List<ProgressInfo> progressInfos) {
        return new ProgressInfo("Task status", getProcessingStatus(progressInfos));
    }
}
