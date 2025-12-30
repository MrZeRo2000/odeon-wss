package com.romanpulov.odeonwss.serializer;

import com.romanpulov.odeonwss.entity.ArtistType;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class ArtistTypeDeserializer extends StdDeserializer<ArtistType> {
    public ArtistTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ArtistType deserialize(JsonParser p, DeserializationContext ctxt) {
        return ArtistType.fromCode(p.readValueAs(String.class));
    }
}
