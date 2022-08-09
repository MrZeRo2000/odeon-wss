package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ArtistsMDBImportProcessor extends AbstractMDBImportProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ArtistsMDBImportProcessor.class);

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    public ArtistsMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository
    ) {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        ArtistsMDBImporter artistsMDBImporter = new ArtistsMDBImporter(
                artistRepository,
                artistDetailRepository,
                artistCategoryRepository,
                artistLyricsRepository,
                mdbReader,
                ArtistType.ARTIST
        );

        infoHandler(ProcessorMessages.INFO_ARTISTS_IMPORTED, artistsMDBImporter.importArtists());
        infoHandler(ProcessorMessages.INFO_ARTIST_DETAILS_IMPORTED, artistsMDBImporter.importArtistDetails());
        infoHandler(ProcessorMessages.INFO_ARTIST_CATEGORIES_IMPORTED, artistsMDBImporter.importArtistCategories());
        infoHandler(ProcessorMessages.INFO_ARTIST_LYRICS_IMPORTED, artistsMDBImporter.importArtistLyrics());
    }
}
