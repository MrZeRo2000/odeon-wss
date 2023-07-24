package com.romanpulov.odeonwss.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.romanpulov.odeonwss.entity.ArtistCategoryType;

import java.io.IOException;

public class ArtistCategoryTypeSerializer extends StdSerializer<ArtistCategoryType> {
    public ArtistCategoryTypeSerializer(Class<ArtistCategoryType> t) {
        super(t);
    }

    public ArtistCategoryTypeSerializer() {
        this(null);
    }

    @Override
    public void serialize(ArtistCategoryType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getCode());
    }
}
