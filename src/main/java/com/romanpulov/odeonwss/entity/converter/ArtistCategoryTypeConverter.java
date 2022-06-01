package com.romanpulov.odeonwss.entity.converter;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class ArtistCategoryTypeConverter implements AttributeConverter<ArtistCategoryType, String> {
    @Override
    public String convertToDatabaseColumn(ArtistCategoryType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public ArtistCategoryType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            return ArtistCategoryType.fromCode(dbData);
        }
    }
}
