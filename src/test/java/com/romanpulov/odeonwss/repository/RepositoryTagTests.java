package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.builder.entitybuilder.EntityTagBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;

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
        Assertions.assertEquals(1, tagRepository.count());
        Assertions.assertEquals(1, tag1.getId());
        Assertions.assertNotNull(tag1.getInsertDateTime());
        Assertions.assertNotNull(tag1.getUpdateDateTime());
        Assertions.assertEquals(tag1.getInsertDateTime(), tag1.getUpdateDateTime());

        Assertions.assertThrows(JpaSystemException.class,() -> tagRepository.save(tag11));
        Assertions.assertEquals(1, tagRepository.count());

        tagRepository.save(tag2);
        Assertions.assertEquals(2, tagRepository.count());

        Assertions.assertTrue(tagRepository.findTagByName("stone").isPresent());
        Assertions.assertTrue(tagRepository.findTagByName("water").isPresent());
        Assertions.assertFalse(tagRepository.findTagByName("wind").isPresent());
    }
}
