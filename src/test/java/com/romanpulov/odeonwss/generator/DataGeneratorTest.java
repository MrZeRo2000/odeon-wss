package com.romanpulov.odeonwss.generator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataGeneratorTest {

    @Autowired
    DataGenerator dataGenerator;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateArtists() throws Exception {
        String json =
"""
{
    "artists": [
        {
            "artistType": "A",
            "artistName": "Ozzy Osbourne"
        },
        {
            "artistType": "A",
            "artistName": "A-HA"
        }
    ]
}
""";
        dataGenerator.generateFromJSON(json);
    }
}
