package com.romanpulov.odeonwss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OdeonWssApplication {

	public static void main(String[] args) {
		SpringApplication.run(OdeonWssApplication.class, args);
	}

}
