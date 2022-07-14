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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceProcessValidateLATest {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ProcessService processService;

    @Autowired
    EntityManager em;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testEmptyShouldFail() {
        List.of("Evanescence", "Pink Floyd", "Therapy", "Tori Amos", "Abigail Williams", "Agua De Annique", "Christina Aguilera")
                .forEach(s -> artistRepository.save(
                        new EntityArtistBuilder().withType(ArtistType.ARTIST).withName(s).build()
                ));

        List<ProgressDetail> progressDetail;

        processService.executeProcessor(ProcessorType.LA_VALIDATOR, null);
        progressDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(ProcessingStatus.FAILURE, processService.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(2)
    void testLoad() {
        List<ProgressDetail> progressDetail;

        processService.executeProcessor(ProcessorType.LA_LOADER, null);
        progressDetail = processService.getProcessInfo().getProgressDetails();

        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(3)
    void testOk() throws Exception {
        processService.executeProcessor(ProcessorType.LA_VALIDATOR);
        List<ProgressDetail> progressDetail = processService.getProcessInfo().getProgressDetails();
        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(4)
    @Transactional
    void testNoCompositionMediaFileShouldFail() throws Exception {
        em.createNativeQuery("delete from compositions_media_files WHERE comp_id = 1").executeUpdate();
        processService.executeProcessor(ProcessorType.LA_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.FAILURE, processService.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(5)
    @Transactional
    void testNoCompositionShouldFail() throws Exception {
        em.createNativeQuery("delete from compositions WHERE comp_id = 1").executeUpdate();
        processService.executeProcessor(ProcessorType.LA_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.FAILURE, processService.getProcessInfo().getProcessingStatus());
    }

    @Test
    @Order(6)
    void testOkAgain() throws Exception {
        processService.executeProcessor(ProcessorType.LA_VALIDATOR);
        Assertions.assertEquals(ProcessingStatus.SUCCESS, processService.getProcessInfo().getProcessingStatus());
    }
}
