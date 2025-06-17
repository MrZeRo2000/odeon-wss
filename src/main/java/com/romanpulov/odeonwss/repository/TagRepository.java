package com.romanpulov.odeonwss.repository;

import com.romanpulov.odeonwss.dto.IdNameDTO;
import com.romanpulov.odeonwss.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findTagByName(String name);

    @Query("SELECT t.id AS id, t.name AS name FROM Tag t ORDER BY t.name")
    List<IdNameDTO> findAllDTO();
}
