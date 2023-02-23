package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProcessingStatusConverter implements AttributeConverter<ProcessingStatus, String> {
    @Override
    public String convertToDatabaseColumn(ProcessingStatus status) {
        return status == null ? null : status.toString();
    }

    @Override
    public ProcessingStatus convertToEntityAttribute(String s) {
        try {
            return ProcessingStatus.valueOf(s);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
