package com.romanpulov.odeonwss.service.processor.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ProcessDetail {

    private final LocalDateTime time;

    public LocalDateTime getTime() {
        return time;
    }

    private final ProcessDetailInfo info;

    public ProcessDetailInfo getInfo() {
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

    public ProcessDetail(LocalDateTime time, ProcessDetailInfo info, ProcessingStatus status, Integer rows, ProcessingAction processingAction) {
        this.time = time;
        this.info = info;
        this.status = status;
        this.rows = rows;
        this.processingAction = processingAction;
    }

    public ProcessDetail(ProcessDetailInfo info, ProcessingStatus status, Integer rows, ProcessingAction processingAction) {
        this(LocalDateTime.now(), info, status, rows, processingAction);
    }

    public static ProcessDetail fromMessageAndStatus(String message, ProcessingStatus status) {
        return new ProcessDetail(ProcessDetailInfo.fromMessage(message), status, null, null);
    }

    public static ProcessDetail fromException(Exception e) {
        return fromErrorMessage(e.getMessage() == null ? "Unsupported operation" : e.getMessage());
    }

    public static ProcessDetail fromInfoMessage(String infoMessage) {
        return fromMessageAndStatus(infoMessage, ProcessingStatus.INFO);
    }

    public static ProcessDetail fromInfoMessage(String infoMessage, int rows) {
        return new ProcessDetail(ProcessDetailInfo.fromMessage(infoMessage), ProcessingStatus.INFO, rows, null);
    }

    public static ProcessDetail fromInfoMessage(String errorMessage, String s) {
        return fromInfoMessage(String.format(errorMessage, s));
    }

    public static ProcessDetail fromErrorMessage(String errorMessage) {
        return fromMessageAndStatus(errorMessage, ProcessingStatus.FAILURE);
    }

    public static ProcessDetail fromErrorMessage(String errorMessage, List<String> items) {
        return new ProcessDetail(ProcessDetailInfo.fromMessageItems(errorMessage, items), ProcessingStatus.FAILURE, null, null);
    }

    public static ProcessDetail fromErrorMessage(String errorMessage, Object ...args) {
        return fromErrorMessage(String.format(errorMessage, args));
    }

    public static ProcessDetail fromWarningMessage(String warningMessage) {
        return fromMessageAndStatus(warningMessage, ProcessingStatus.WARNING);
    }

    public static ProcessDetail fromWarningMessageWithAction(String warningMessage, ProcessingActionType processingActionType, String actionValue) {
        return new ProcessDetail(ProcessDetailInfo.fromMessage(warningMessage), ProcessingStatus.WARNING, null, new ProcessingAction(processingActionType, actionValue));
    }

    public static ProcessingStatus getFinalProcessingStatus(List<ProcessDetail> processDetails) {
        List<ProcessingStatus> processingStatuses = processDetails.stream().map(p -> p.status).distinct().toList();
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
