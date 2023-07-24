package com.romanpulov.odeonwss.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;

import java.io.IOException;

public class ArtistCategoryTypeDeserializer extends StdDeserializer<ArtistCategoryType> {
    public ArtistCategoryTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    public ArtistCategoryTypeDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public ArtistCategoryType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ArtistCategoryType.fromCode(p.readValueAs(String.class));
    }
}
