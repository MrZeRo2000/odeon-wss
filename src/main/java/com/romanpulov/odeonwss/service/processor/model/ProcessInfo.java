package com.romanpulov.odeonwss.service.processor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProcessInfo {
    private final ProcessorType processorType;

    public ProcessorType getProcessorType() {
        return processorType;
    }

    private ProcessingStatus processingStatus;

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        this.lastUpdated = LocalDateTime.now();
    }

    private LocalDateTime lastUpdated;

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    private final List<ProcessDetail> processDetails = new ArrayList<>();

    public List<ProcessDetail> getProgressDetails() {
        return processDetails;
    }

    public ProcessDetail getLastProgressDetail() {
        if (processDetails.size() < 2) {
            return null;
        } else {
            return processDetails.get(processDetails.size() - 2);
        }
    }

    public void resolveAction(ProcessingAction processingAction) {
        processDetails.stream()
                .filter(d -> processingAction.equals(d.getProcessingAction()))
                .findFirst()
                .ifPresent(processDetails::remove);
    }

    public ProcessInfo(ProcessorType processorType) {
        this.processorType = processorType;
        this.processingStatus = ProcessingStatus.IN_PROGRESS;
        this.lastUpdated = LocalDateTime.now();
    }

    public void addProgressDetails(ProcessDetail processDetail) {
        processDetails.add(processDetail);
        lastUpdated = LocalDateTime.now();
    }

    public void addProgressDetailsErrorItem(String message, String item) {
        ProcessDetail processDetail = processDetails
                .stream()
                .filter(d -> d.getInfo().getMessage().equals(message))
                .findFirst()
                .orElseGet(() -> {
                    ProcessDetail newProcessDetail = ProcessDetail.fromErrorMessage(message);
                    processDetails.add(newProcessDetail);
                    return newProcessDetail;
                });
        processDetail.getInfo().getItems().add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessInfo that = (ProcessInfo) o;
        return processorType == that.processorType && processingStatus == that.processingStatus && lastUpdated.equals(that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processorType, processingStatus, lastUpdated);
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "processorType=" + processorType +
                ", processingStatus=" + processingStatus +
                ", lastUpdated=" + lastUpdated +
                ", progressDetails=" + processDetails.stream().map(Object::toString).collect(Collectors.joining("-", "{", "}")) +
                '}';
    }
}
