package com.romanpulov.odeonwss.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Converter
public class DateTimeConverter implements AttributeConverter<LocalDateTime, Long> {
    @Override
    public Long convertToDatabaseColumn(LocalDateTime localDateTime) {
        return localDateTime == null? null : localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long aLong) {
        return aLong == null ? null : Instant.ofEpochSecond(aLong).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }
}
