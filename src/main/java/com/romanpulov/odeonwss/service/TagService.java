package com.romanpulov.odeonwss.service;

import com.romanpulov.odeonwss.entity.Tag;
import com.romanpulov.odeonwss.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public Set<Tag> getOrCreateTags(Collection<String> tagNames) {
        return tagNames
                .stream()
                .map(v -> repository.findTagByName(v).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(v);
                    repository.save(newTag);
                    return newTag;
                }))
                .collect(Collectors.toSet());
    }
}
