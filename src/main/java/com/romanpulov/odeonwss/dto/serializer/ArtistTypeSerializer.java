package com.romanpulov.odeonwss.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.romanpulov.odeonwss.entity.ArtistType;

import java.io.IOException;

public class ArtistTypeSerializer extends StdSerializer<ArtistType> {
    public ArtistTypeSerializer(Class<ArtistType> t) {
        super(t);
    }

    public ArtistTypeSerializer() {
        this(null);
    }

    @Override
    public void serialize(ArtistType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getCode());
    }
}
