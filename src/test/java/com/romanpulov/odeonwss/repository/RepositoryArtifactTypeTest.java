package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.ArtifactType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryArtifactTypeTest {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    ArtifactTypeRepository artifactTypeRepository;

    @Test
    void testData() {
        List<ArtifactType> artifactTypes = new ArrayList<>();
        artifactTypeRepository.findAll().forEach(artifactTypes::add);

        assertThat(artifactTypes.size()).isEqualTo(9);
    }

    @Test
    void testByNameIn() {
        assertThat(artifactTypeRepository.getAllByIdIsIn(List.of(101L, 102L)).size()).isEqualTo(2);
        assertThat(artifactTypeRepository.getAllByIdIsIn(List.of(101L, 88L)).size()).isEqualTo(1);
        assertThat(artifactTypeRepository.getAllByIdIsIn(List.of(777L)).size()).isEqualTo(0);
    }

    @Test
    void testCaching() {
        Cache cache = cacheManager.getCache("artifactTypeMP3");
        assert cache != null;
        cache.invalidate();

        assertThat(cache.get("default")).isNull();
        artifactTypeRepository.getWithMP3();
        assertThat(cache.get("default")).isNotNull();
    }
}
