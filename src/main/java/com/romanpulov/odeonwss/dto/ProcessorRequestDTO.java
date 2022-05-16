package com.romanpulov.odeonwss.dto;

public class ProcessorRequestDTO {
    private final String processorType;

    public String getProcessorType() {
        return processorType;
    }

    public ProcessorRequestDTO(String processorType) {
        this.processorType = processorType;
    }

    @Override
    public String toString() {
        return "ProcessorRequestDTO{" +
                "processorType='" + processorType + '\'' +
                '}';
    }
}
