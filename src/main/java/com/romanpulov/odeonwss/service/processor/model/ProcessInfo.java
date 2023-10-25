package com.romanpulov.odeonwss.service.processor.model;

import com.romanpulov.odeonwss.service.processor.ProcessorMessages;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProcessInfo {
    private final static int MAX_PROCESS_ITEMS = 10;
    private final static String MAX_PROCESS_ITEMS_STRING = "...";

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

    public List<ProcessDetail> getProcessDetails() {
        return processDetails;
    }

    private ProcessingEvent processingEvent;

    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    public void clearProcessingEvent() {
        this.processingEvent = null;
    }

    public void resolveAction(ProcessingAction processingAction) {
        processDetails.stream()
                .filter(d -> processingAction.equals(d.getProcessingAction()))
                .findFirst()
                .ifPresent(processDetails::remove);
    }

    public void finalizeProcess() {
        // clear any intermediate statuses
        this.clearProcessingEvent();

        // set final status
        ProcessDetail finalProcessDetail = ProcessDetail.createFinalProgressDetail(getProcessDetails());
        addProcessDetails(finalProcessDetail);
        setProcessingStatus(finalProcessDetail.getStatus());

        if (processingStatus.equals(ProcessingStatus.SUCCESS)) {
            processingEvent = ProcessingEvent.fromEventText(ProcessorMessages.PROCESSING_COMPLETED);
        }

        // truncate long items
        getProcessDetails().forEach(processDetail -> {
            List<String> items = processDetail.getInfo().getItems();

            if (items.size() > MAX_PROCESS_ITEMS) {
                items.subList(MAX_PROCESS_ITEMS, items.size()).clear();
                items.set(items.size() - 1, MAX_PROCESS_ITEMS_STRING);
            }
        });
    }

    public ProcessInfo(ProcessorType processorType) {
        this.processorType = processorType;
        this.processingStatus = ProcessingStatus.IN_PROGRESS;
        this.lastUpdated = LocalDateTime.now();
    }

    public void addProcessDetails(ProcessDetail processDetail) {
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
                ", processingEvent=" + processingEvent +
                '}';
    }
}
