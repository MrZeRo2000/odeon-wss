package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProcessorTypeConverter implements AttributeConverter<ProcessorType, String> {
    @Override
    public String convertToDatabaseColumn(ProcessorType processorType) {
        return processorType == null ? null : processorType.toString();
    }

    @Override
    public ProcessorType convertToEntityAttribute(String s) {
        try {
            return ProcessorType.valueOf(s);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
