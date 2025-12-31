package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.entity.converter.DateTimeConverter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitDateTimeConverterTest {

    final DateTimeConverter dtc = new DateTimeConverter();

    @Test
    void testMain() {
        LocalDateTime dt = LocalDateTime.of(2020, 4,12, 15, 13, 45);
        Long longDt = dtc.convertToDatabaseColumn(dt);
        LocalDateTime convertedDt = dtc.convertToEntityAttribute(longDt);

        assertThat(dt).isEqualTo(convertedDt);
    }

    @Test
    void test2000() {
        LocalDateTime dt = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        assertThat(dtc.convertToDatabaseColumn(dt)).isEqualTo(946684800L);

        LocalDateTime convertedDt = dtc.convertToEntityAttribute(946684800L);
        assertThat(convertedDt).isEqualTo(dt);
    }

}
