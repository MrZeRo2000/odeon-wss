package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.service.processor.model.ProcessorType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class ProcessorTypeConverter implements AttributeConverter<ProcessorType, String> {
    @Override
    public String convertToDatabaseColumn(ProcessorType processorType) {
        return processorType == null ? null : processorType.getLabel();
    }

    @Override
    public ProcessorType convertToEntityAttribute(String s) {
        return Stream.of(ProcessorType.values())
                .filter(p -> p.getLabel().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
