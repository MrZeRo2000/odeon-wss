package com.romanpulov.odeonwss.dto.process;

import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessDetailDTOImpl implements ProcessDetailDTO {
    private Long id;
    private LocalDateTime updateDateTime;
    private ProcessingStatus status;
    private String message;
    private Long rows;
    private List<String> items = new ArrayList<>();
    private ProcessingActionDTO processingAction;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @Override
    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }

    @Override
    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public ProcessingActionDTO getProcessingAction() {
        return processingAction;
    }

    public void setProcessingAction(ProcessingActionDTO processingAction) {
        this.processingAction = processingAction;
    }

    @Override
    public String toString() {
        return "ProcessDetailDTOImpl{" +
                "updateDateTime=" + updateDateTime +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", rows=" + rows +
                ", items=" + items +
                ", processingAction=" + processingAction +
                '}';
    }
}
