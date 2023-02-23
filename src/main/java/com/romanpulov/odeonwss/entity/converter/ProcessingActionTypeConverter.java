package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProcessingActionTypeConverter implements AttributeConverter<ProcessingActionType, String> {
    @Override
    public String convertToDatabaseColumn(ProcessingActionType processingActionType) {
        return processingActionType == null ? null : processingActionType.toString();
    }

    @Override
    public ProcessingActionType convertToEntityAttribute(String s) {
        try {
            return ProcessingActionType.valueOf(s);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
