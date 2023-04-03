package com.romanpulov.odeonwss.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceDVProductTest {
    private static final Logger log = Logger.getLogger(ServiceDVProductTest.class.getSimpleName());

    @Autowired
    private DVProductService service;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void insertNewWithoutCategoriesShouldBeOk() throws Exception {
    }
}
