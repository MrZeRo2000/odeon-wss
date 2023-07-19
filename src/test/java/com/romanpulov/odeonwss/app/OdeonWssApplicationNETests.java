package com.romanpulov.odeonwss.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test-ne")
class OdeonWssApplicationNETests {
	@Test
	void contextLoads() {
	}
}
