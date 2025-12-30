package com.romanpulov.odeonwss.serializer;

import com.romanpulov.odeonwss.entity.ArtistType;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class ArtistTypeSerializer extends StdSerializer<ArtistType> {
    public ArtistTypeSerializer(Class<ArtistType> t) {
        super(t);
    }

    public ArtistTypeSerializer() {
        this(null);
    }

    @Override
    public void serialize(ArtistType value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.getCode());
    }
}
