package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.entity.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findTagByName(String name);
}
