package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.entity.ArtistType;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class ArtistTypeConverter implements AttributeConverter<ArtistType, String> {
    @Override
    public String convertToDatabaseColumn(ArtistType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public ArtistType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            return Stream.of(ArtistType.values())
                    .filter(artistType -> artistType.getCode().equals(dbData))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}
