package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.entity.converter.DateTimeConverter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitDateTimeConverterTest {

    DateTimeConverter dtc = new DateTimeConverter();

    @Test
    void testMain() {
        LocalDateTime dt = LocalDateTime.of(2020, 4,12, 15, 13, 45);
        Long longDt = dtc.convertToDatabaseColumn(dt);
        LocalDateTime convertedDt = dtc.convertToEntityAttribute(longDt);

        assertThat(dt).isEqualTo(convertedDt);
    }

}
