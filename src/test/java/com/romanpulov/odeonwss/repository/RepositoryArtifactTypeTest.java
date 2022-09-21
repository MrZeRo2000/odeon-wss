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

        Assertions.assertEquals(7, artifactTypes.size());
    }

    @Test
    void testByNameIn() {
        Assertions.assertEquals(2, artifactTypeRepository.getAllByIdIsIn(List.of(101L, 102L)).size());
        Assertions.assertEquals(1, artifactTypeRepository.getAllByIdIsIn(List.of(101L, 88L)).size());
        Assertions.assertEquals(0, artifactTypeRepository.getAllByIdIsIn(List.of(777L)).size());
    }
}
