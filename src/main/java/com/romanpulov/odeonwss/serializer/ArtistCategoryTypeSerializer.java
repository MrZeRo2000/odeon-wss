package com.romanpulov.odeonwss.serializer;

import com.romanpulov.odeonwss.entity.ArtistCategoryType;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class ArtistCategoryTypeSerializer extends StdSerializer<ArtistCategoryType> {
    public ArtistCategoryTypeSerializer(Class<ArtistCategoryType> t) {
        super(t);
    }

    public ArtistCategoryTypeSerializer() {
        this(null);
    }

    @Override
    public void serialize(ArtistCategoryType value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.getCode());
    }
}
