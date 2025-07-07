package com.romanpulov.odeonwss.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanpulov.odeonwss.dto.ArtistDTO;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataGenerator {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ArtistRepository artistRepository;

    public void generateFromJSON(String json) throws JsonProcessingException {
        DataGeneratorDTO data = mapper.readValue(json, DataGeneratorDTO.class);

        Map<String, Artist> artist = data.getArtists() == null ? null : createArtists(data.getArtists());
    }

    private Map<String, Artist> createArtists(Collection<ArtistDTO> artists) {
        Map<String, Artist> result = new HashMap<>();
        for (ArtistDTO artist : artists) {
            if ((artist.getArtistName() != null) && !artist.getArtistName().isEmpty()) {
                Artist newArtist = new Artist();
                newArtist.setName(artist.getArtistName());
                newArtist.setType(artist.getArtistType());

                artistRepository.save(newArtist);

                result.put(artist.getArtistName(), newArtist);
            }
        }

        return result;
    }
}
