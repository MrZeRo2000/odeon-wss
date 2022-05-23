package com.romanpulov.odeonwss.dto;

public class ProcessorRequestDTO {
    private String processorType;

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public ProcessorRequestDTO() {}

    public static ProcessorRequestDTO fromProcessorType(String processorType) {
        ProcessorRequestDTO instance = new ProcessorRequestDTO();
        instance.setProcessorType(processorType);
        return instance;
    }

    @Override
    public String toString() {
        return "ProcessorRequestDTO{" +
                "processorType='" + processorType + '\'' +
                '}';
    }
}
