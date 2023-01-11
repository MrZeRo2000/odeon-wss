package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.entity.ArtistType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
            return ArtistType.fromCode(dbData);
        }
    }
}
