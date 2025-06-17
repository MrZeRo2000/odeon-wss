package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityTagBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryTagTests {
    @Autowired
    private TagRepository tagRepository;

    @Test
    @Order(1)
    @Sql({"/schema.sql", "/data.sql"})
    void testInsertGet() {
        var tag1 = new EntityTagBuilder().withName("stone").build();
        var tag11 = new EntityTagBuilder().withName("stone").build();
        var tag2 = new EntityTagBuilder().withName("water").build();

        tagRepository.save(tag1);
        assertThat(tagRepository.count()).isEqualTo(1);
        assertThat(tag1.getId()).isEqualTo(1);
        assertThat(tag1.getInsertDateTime()).isNotNull();
        assertThat(tag1.getUpdateDateTime()).isNotNull();
        assertThat(tag1.getInsertDateTime()).isEqualTo(tag1.getUpdateDateTime());

        Assertions.assertThrows(JpaSystemException.class,() -> tagRepository.save(tag11));
        assertThat(tagRepository.count()).isEqualTo(1);

        tagRepository.save(tag2);
        assertThat(tagRepository.count()).isEqualTo(2);

        assertThat(tagRepository.findTagByName("stone")).isPresent();
        assertThat(tagRepository.findTagByName("water")).isPresent();
        assertThat(tagRepository.findTagByName("wind")).isNotPresent();

        tagRepository.save(new EntityTagBuilder().withName("paper").build());

        var orderedByName = tagRepository.findAllDTO();
        assertThat(orderedByName.size()).isEqualTo(3);

        assertThat(orderedByName.get(0).getName()).isEqualTo("paper");
        assertThat(orderedByName.get(1).getName()).isEqualTo("stone");
        assertThat(orderedByName.get(2).getName()).isEqualTo("water");
    }
}
