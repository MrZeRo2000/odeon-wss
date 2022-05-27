package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.repository.ArtifactTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryArtifactTypeTest {

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Test
    void testData() {
        List<ArtifactType> artifactTypes = new ArrayList<>();
        artifactTypeRepository.findAll().forEach(artifactTypes::add);

        Assertions.assertEquals(3, artifactTypes.size());
    }
}