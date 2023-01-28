package com.romanpulov.odeonwss.service.processor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProgressDetail {

    public static class ProgressInfo {
        private final String message;

        public String getMessage() {
            return message;
        }

        private final List<String> items;

        public List<String> getItems() {
            return items;
        }

        public ProgressInfo(String message, List<String> items) {
            this.message = message;
            this.items = items;
        }

        public static ProgressInfo fromMessage(String message) {
            return new ProgressInfo(message, new ArrayList<>());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProgressInfo that = (ProgressInfo) o;
            return message.equals(that.message) && Objects.equals(items, that.items);
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

    private final ProgressInfo info;

    public ProgressInfo getInfo() {
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

    public ProgressDetail(ProgressInfo info, ProcessingStatus status, Integer rows, ProcessingAction processingAction) {
        this.time = LocalDateTime.now();
        this.info = info;
        this.status = status;
        this.rows = rows;
        this.processingAction = processingAction;
    }

    public static ProgressDetail fromMessageAndStatus(String message, ProcessingStatus status) {
        return new ProgressDetail(ProgressInfo.fromMessage(message), status, null, null);
    }

    public static ProgressDetail fromException(Exception e) {
        return fromErrorMessage(e.getMessage() == null ? "Unsupported operation" : e.getMessage());
    }

    public static ProgressDetail fromInfoMessage(String infoMessage) {
        return fromMessageAndStatus(infoMessage, ProcessingStatus.INFO);
    }

    public static ProgressDetail fromInfoMessage(String infoMessage, int rows) {
        return new ProgressDetail(ProgressInfo.fromMessage(infoMessage), ProcessingStatus.INFO, rows, null);
    }

    public static ProgressDetail fromInfoMessage(String errorMessage, Object ...args) {
        return fromInfoMessage(String.format(errorMessage, args));
    }

    public static ProgressDetail fromErrorMessage(String errorMessage) {
        return fromMessageAndStatus(errorMessage, ProcessingStatus.FAILURE);
    }

    public static ProgressDetail fromErrorMessage(String errorMessage, List<String> items) {
        return new ProgressDetail(new ProgressInfo(errorMessage, items), ProcessingStatus.FAILURE, null, null);
    }

    public static ProgressDetail fromErrorMessage(String errorMessage, Object ...args) {
        return fromErrorMessage(String.format(errorMessage, args));
    }

    public static ProgressDetail fromWarningMessage(String warningMessage) {
        return fromMessageAndStatus(warningMessage, ProcessingStatus.WARNING);
    }

    public static ProgressDetail fromWarningMessage(String warningMessage, Object ...args) {
        return fromWarningMessage(String.format(warningMessage, args));
    }

    public static ProgressDetail fromWarningMessageWithAction(String warningMessage, ProcessingActionType processingActionType, String actionValue) {
        return new ProgressDetail(ProgressInfo.fromMessage(warningMessage), ProcessingStatus.WARNING, null, new ProcessingAction(processingActionType, actionValue));
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
        return fromMessageAndStatus("Task status", getFinalProcessingStatus(progressDetails));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgressDetail that = (ProgressDetail) o;
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
