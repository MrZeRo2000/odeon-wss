package com.romanpulov.odeonwss.dto.process;

import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessInfoDTOImpl implements ProcessInfoDTO {
    private Long id;
    private ProcessorType processorType;
    private ProcessingStatus processingStatus;
    private LocalDateTime updateDateTime;
    private List<ProcessDetailDTO> processDetails = new ArrayList<>();
    private ProcessingEventDTO processingEvent;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ProcessorType getProcessorType() {
        return processorType;
    }

    public void setProcessorType(ProcessorType processorType) {
        this.processorType = processorType;
    }

    @Override
    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    @Override
    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @Override
    public List<ProcessDetailDTO> getProcessDetails() {
        return processDetails;
    }

    public void setProcessDetails(List<ProcessDetailDTO> processDetails) {
        this.processDetails = processDetails;
    }

    @Override
    public ProcessingEventDTO getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEventDTO processingEvent) {
        this.processingEvent = processingEvent;
    }

    @Override
    public String toString() {
        return "ProcessInfoDTOImpl{" +
                "id=" + id +
                ", processorType=" + processorType +
                ", processingStatus=" + processingStatus +
                ", updateDateTime=" + updateDateTime +
                ", processDetails=" + processDetails +
                ", processingEvent=" + processingEvent +
                '}';
    }
}
