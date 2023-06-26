package com.romanpulov.odeonwss.dto;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TrackTransformer {
    public List<TrackDTO> transform(List<TrackFlatDTO> rs) {
        final Map<Long, TrackDTOImpl> trackDTOMap = new LinkedHashMap<>();
        final Map<Long, ArtistDTO> artistDTOMap = new HashMap<>();
        final Map<Long, IdNameDTO> dvTypeDTOMap = new HashMap<>();

        for (TrackFlatDTO row: rs) {
            TrackDTOImpl dto = Optional.ofNullable(trackDTOMap.get(row.getId())).orElseGet(() -> {
                TrackDTOImpl newDTO = new TrackDTOImpl();
                newDTO.setId(row.getId());
                newDTO.setArtifactId(row.getArtifactId());
                newDTO.setArtifactTitle(row.getArtifactTitle());

                if (row.getArtistId() != null) {
                    newDTO.setArtist(Optional.ofNullable(artistDTOMap.get(row.getArtistId())).orElseGet(() -> {
                        ArtistDTOImpl newArtistDTO = new ArtistDTOImpl();
                        newArtistDTO.setId(row.getArtistId());
                        newArtistDTO.setArtistName(row.getArtistName());

                        artistDTOMap.putIfAbsent(row.getArtistId(), newArtistDTO);
                        return newArtistDTO;
                    }));
                }

                if (row.getPerformerArtistId() != null) {
                    newDTO.setPerformerArtist(Optional.ofNullable(artistDTOMap.get(row.getPerformerArtistId())).orElseGet(() -> {
                        ArtistDTOImpl newArtistDTO = new ArtistDTOImpl();
                        newArtistDTO.setId(row.getPerformerArtistId());
                        newArtistDTO.setArtistName(row.getPerformerArtistName());

                        artistDTOMap.putIfAbsent(row.getArtistId(), newArtistDTO);
                        return newArtistDTO;
                    }));
                }

                if (row.getDvTypeId() != null) {
                    newDTO.setDvType(Optional.ofNullable(dvTypeDTOMap.get(row.getDvTypeId())).orElseGet(() -> {
                        IdNameDTOImpl newDvTypeDTO = new IdNameDTOImpl();
                        newDvTypeDTO.setId(row.getDvTypeId());
                        newDvTypeDTO.setName(row.getDvTypeName());

                        dvTypeDTOMap.putIfAbsent(row.getDvTypeId(), newDvTypeDTO);
                        return newDvTypeDTO;
                    }));
                }

                if ((row.getDvProductId() != null) && (row.getDvProductTitle() != null)) {
                    DVProductDTOImpl dvProductDTO = new DVProductDTOImpl();
                    dvProductDTO.setId(row.getDvProductId());
                    dvProductDTO.setTitle(row.getDvProductTitle());
                    
                    newDTO.setDvProduct(dvProductDTO);
                }

                newDTO.setTitle(row.getTitle());
                newDTO.setDuration(row.getDuration());
                newDTO.setDiskNum(row.getDiskNum());
                newDTO.setNum(row.getNum());

                trackDTOMap.put(row.getId(), newDTO);
                return newDTO;
            });

            if (row.getFileName() != null) {
                dto.getFileNames().add(row.getFileName());
            }

            //accumulate size
            if (row.getSize() != null) {
                dto.setSize(Optional.ofNullable(dto.getSize()).orElse(0L) + row.getSize());
            }

            //accumulate bitrate
            if (row.getBitRate() != null) {
                dto.setBitRate(Optional.ofNullable(dto.getBitRate()).orElse(0L) + row.getBitRate());
            }

        }

        // calc average bitrate
        trackDTOMap.values().forEach(t -> {
            int files = t.getFileNames().size();
            if ((files > 0) && (t.getBitRate() != null)) {
                t.setBitRate(t.getBitRate() / files);
            }
        });

        return new ArrayList<>(trackDTOMap.values());
    }
}
