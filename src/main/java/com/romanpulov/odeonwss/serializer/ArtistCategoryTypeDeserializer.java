package com.romanpulov.odeonwss.serializer;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ArtistCategoryTypeDeserializer extends StdDeserializer<ArtistCategoryType> {
    public ArtistCategoryTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    public ArtistCategoryTypeDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public ArtistCategoryType deserialize(JsonParser p, DeserializationContext ctxt) {
        return ArtistCategoryType.fromCode(p.readValueAs(String.class));
    }
}
