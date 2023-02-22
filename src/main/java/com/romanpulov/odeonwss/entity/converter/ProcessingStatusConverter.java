package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class ProcessingStatusConverter implements AttributeConverter<ProcessingStatus, String> {
    @Override
    public String convertToDatabaseColumn(ProcessingStatus status) {
        return status == null ? null : status.getLabel();
    }

    @Override
    public ProcessingStatus convertToEntityAttribute(String s) {
        return Stream.of(ProcessingStatus.values())
                .filter(p -> p.getLabel().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
