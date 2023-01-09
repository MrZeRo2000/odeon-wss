package com.romanpulov.odeonwss.processor;

import com.romanpulov.odeonwss.service.processor.ProcessorFactory;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProcessorFactoryTest {

    @Autowired
    ProcessorFactory processorFactory;

    @Test
    void testProcessorTypeCreation() {
        for (ProcessorType processorType: ProcessorType.values()) {
            Assertions.assertNotNull(processorFactory.fromProcessorType(processorType, null));
        }
    }
}
