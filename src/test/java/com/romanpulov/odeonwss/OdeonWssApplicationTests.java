package com.romanpulov.odeonwss;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OdeonWssApplicationTests {

	@Test
	@Sql({"/schema.sql", "/data.sql"})
	void contextLoads() {
	}
}
