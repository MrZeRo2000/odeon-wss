package com.romanpulov.odeonwss.dto.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.io.IOException;

public class ArtistTypeDeserializer extends StdDeserializer<ArtistType> {
    public ArtistTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    public ArtistTypeDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public ArtistType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return ArtistType.fromCode(p.readValueAs(String.class));
    }
}
