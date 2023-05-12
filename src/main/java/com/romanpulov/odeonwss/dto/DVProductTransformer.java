package com.romanpulov.odeonwss.dto;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DVProductTransformer {
    public List<DVProductDTO> transform(List<DVProductFlatDTO> rs) {
        // LinkedHashMap because of a need to retain original order for correct sorting
        final Map<Long, DVProductDTO> productDTOMap = new LinkedHashMap<>();
        final Map<String, DVOriginDTO> originDTOMap = new HashMap<>();

        for (DVProductFlatDTO row : rs) {
            Long id = row.getId();
            DVProductDTO dto = Optional.ofNullable(productDTOMap.get(id)).orElseGet(() -> {
                DVProductDTOImpl newDTO = new DVProductDTOImpl();
                newDTO.setId(row.getId());
                newDTO.setArtifactTypeId(row.getArtifactTypeId());
                newDTO.setDvOrigin(Optional.ofNullable(originDTOMap.get(row.getDvOriginName())).orElseGet(() -> {
                    DVOriginDTOImpl newDvOriginDTO = new DVOriginDTOImpl();
                    newDvOriginDTO.setId(row.getDvOriginId());
                    newDvOriginDTO.setName(row.getDvOriginName());

                    originDTOMap.putIfAbsent(newDvOriginDTO.getName(), newDvOriginDTO);
                    return newDvOriginDTO;
                }));
                newDTO.setTitle(row.getTitle());
                newDTO.setOriginalTitle(row.getOriginalTitle());
                newDTO.setYear(row.getYear());
                newDTO.setFrontInfo(row.getFrontInfo());
                newDTO.setDescription(row.getDescription());
                newDTO.setHasDescription(TransformRules.booleanFromLong(row.getHasDescription()));
                newDTO.setNotes(row.getNotes());
                newDTO.setHasNotes(TransformRules.booleanFromLong(row.getHasNotes()));
                newDTO.setHasTracks(TransformRules.booleanFromLong(row.getHasTracks()));

                productDTOMap.put(newDTO.getId(), newDTO);
                return newDTO;
            });

            if ((row.getDvCategoryId() != null) || (row.getDvCategoryName() != null)) {
                DVCategoryDTOImpl newDvCategoryDTO = new DVCategoryDTOImpl();
                newDvCategoryDTO.setId(row.getDvCategoryId());
                newDvCategoryDTO.setName(row.getDvCategoryName());
                dto.getDvCategories().add(newDvCategoryDTO);
            }
        }

        return new ArrayList<>(productDTOMap.values());
    }
}
