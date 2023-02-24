package com.romanpulov.odeonwss.service.processor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProcessDetail {

    public static class ProcessInfo {
        private final String message;

        public String getMessage() {
            return message;
        }

        private final List<String> items;

        public List<String> getItems() {
            return items;
        }

        public ProcessInfo(String message, List<String> items) {
            this.message = message;
            this.items = items;
        }

        public static ProcessInfo fromMessage(String message) {
            return new ProcessInfo(message, new ArrayList<>());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProcessInfo that = (ProcessInfo) o;
            return message.equals(that.message) && items.equals(that.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, items);
        }

        @Override
        public String toString() {
            return "ProgressInfo{" +
                    "message='" + message + '\'' +
                    ", items=" + items +
                    '}';
        }
    }

    private final LocalDateTime time;

    public LocalDateTime getTime() {
        return time;
    }

    private final ProcessInfo info;

    public ProcessInfo getInfo() {
        return info;
    }

    private final ProcessingStatus status;

    public ProcessingStatus getStatus() {
        return status;
    }

    private final Integer rows;

    public Integer getRows() {
        return rows;
    }

    private final ProcessingAction processingAction;

    public ProcessingAction getProcessingAction() {
        return processingAction;
    }

    public ProcessDetail(LocalDateTime time, ProcessInfo info, ProcessingStatus status, Integer rows, ProcessingAction processingAction) {
        this.time = time;
        this.info = info;
        this.status = status;
        this.rows = rows;
        this.processingAction = processingAction;

    }

    public ProcessDetail(ProcessInfo info, ProcessingStatus status, Integer rows, ProcessingAction processingAction) {
        this(LocalDateTime.now(), info, status, rows, processingAction);
    }

    public static ProcessDetail fromMessageAndStatus(String message, ProcessingStatus status) {
        return new ProcessDetail(ProcessInfo.fromMessage(message), status, null, null);
    }

    public static ProcessDetail fromException(Exception e) {
        return fromErrorMessage(e.getMessage() == null ? "Unsupported operation" : e.getMessage());
    }

    public static ProcessDetail fromInfoMessage(String infoMessage) {
        return fromMessageAndStatus(infoMessage, ProcessingStatus.INFO);
    }

    public static ProcessDetail fromInfoMessage(String infoMessage, int rows) {
        return new ProcessDetail(ProcessInfo.fromMessage(infoMessage), ProcessingStatus.INFO, rows, null);
    }

    public static ProcessDetail fromInfoMessage(String errorMessage, Object ...args) {
        return fromInfoMessage(String.format(errorMessage, args));
    }

    public static ProcessDetail fromErrorMessage(String errorMessage) {
        return fromMessageAndStatus(errorMessage, ProcessingStatus.FAILURE);
    }

    public static ProcessDetail fromErrorMessage(String errorMessage, List<String> items) {
        return new ProcessDetail(new ProcessInfo(errorMessage, items), ProcessingStatus.FAILURE, null, null);
    }

    public static ProcessDetail fromErrorMessage(String errorMessage, Object ...args) {
        return fromErrorMessage(String.format(errorMessage, args));
    }

    public static ProcessDetail fromWarningMessage(String warningMessage) {
        return fromMessageAndStatus(warningMessage, ProcessingStatus.WARNING);
    }

    public static ProcessDetail fromWarningMessage(String warningMessage, Object ...args) {
        return fromWarningMessage(String.format(warningMessage, args));
    }

    public static ProcessDetail fromWarningMessageWithAction(String warningMessage, ProcessingActionType processingActionType, String actionValue) {
        return new ProcessDetail(ProcessInfo.fromMessage(warningMessage), ProcessingStatus.WARNING, null, new ProcessingAction(processingActionType, actionValue));
    }

    public static ProcessingStatus getFinalProcessingStatus(List<ProcessDetail> processDetails) {
        List<ProcessingStatus> processingStatuses = processDetails.stream().map(p -> p.status).distinct().collect(Collectors.toList());
        if (processingStatuses.contains(ProcessingStatus.FAILURE)) {
            return ProcessingStatus.FAILURE;
        } else if (processingStatuses.contains(ProcessingStatus.WARNING)) {
            return ProcessingStatus.WARNING;
        } else {
            return ProcessingStatus.SUCCESS;
        }
    }

    public static ProcessDetail createFinalProgressDetail(List<ProcessDetail> processDetails) {
        return fromMessageAndStatus("Task status", getFinalProcessingStatus(processDetails));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessDetail that = (ProcessDetail) o;
        return info.equals(that.info) && status == that.status && Objects.equals(rows, that.rows) && Objects.equals(processingAction, that.processingAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, status, rows, processingAction);
    }

    @Override
    public String toString() {
        return "ProgressDetail{" +
                "time=" + time +
                ", info='" + info + '\'' +
                ", rows=" + rows +
                ", status=" + status +
                ", processingAction=" + processingAction +
                '}';
    }
}
