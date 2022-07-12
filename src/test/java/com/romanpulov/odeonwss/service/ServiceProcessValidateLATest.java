package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityArtistBuilder;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.service.processor.model.ProcessingStatus;
import com.romanpulov.odeonwss.service.processor.model.ProcessorType;
import com.romanpulov.odeonwss.service.processor.model.ProgressDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateLATest {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ProcessService processService;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testLoad() {
        List.of("Evanescence", "Pink Floyd", "Therapy", "Tori Amos", "Abigail Williams", "Agua De Annique", "Christina Aguilera")
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        List<ProgressDetail> progressDetail;

        processService.executeProcessor(ProcessorType.LA_LOADER, null);
        progressDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testOk() throws Exception {
        processService.executeProcessor(ProcessorType.LA_VALIDATOR);
        List<ProgressDetail> progressDetail = processService.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
    }
}
