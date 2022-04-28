package com.romanpulov.odeonwss.entity.converter;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class DateConverter implements AttributeConverter<LocalDate, Long> {
    @Override
    public Long convertToDatabaseColumn(LocalDate attribute) {
        return attribute == null? null : attribute.toEpochDay() * 24 * 60 * 60;
    }

    @Override
    public LocalDate convertToEntityAttribute(Long dbData) {
        return dbData == null? null : Instant.ofEpochSecond(dbData).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
