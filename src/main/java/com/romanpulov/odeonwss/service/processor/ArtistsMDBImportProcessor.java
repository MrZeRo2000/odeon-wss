package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.ArtistCategoryRepository;
import com.romanpulov.odeonwss.repository.ArtistDetailRepository;
import com.romanpulov.odeonwss.repository.ArtistLyricsRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.ERROR_PROCESSING_MDB_DATABASE;

@Component
public class ArtistsMDBImportProcessor extends AbstractMDBImportProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ArtistsMDBImportProcessor.class);

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private Map<Long, Artist> migratedArtists;

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
        infoHandler(ProcessorMessages.INFO_ARTISTS_IMPORTED, importArtists(mdbReader));
        infoHandler(ProcessorMessages.INFO_ARTIST_DETAILS_IMPORTED, importArtistDetails(mdbReader));
        infoHandler(ProcessorMessages.INFO_ARTIST_CATEGORIES_IMPORTED, importArtistCategories(mdbReader));
        infoHandler(ProcessorMessages.INFO_ARTIST_LYRICS_IMPORTED, importArtistLyrics(mdbReader));
    }

    private int importArtists(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLIST_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        migratedArtists = new HashMap<>();

        for (Row row: table) {
            ArtistType artistType = row.getInt(SOURCE_COLUMN_NAME) == 5 ? ArtistType.CLASSICS : ArtistType.ARTIST;
            String artistName = row.getString(TITLE_COLUMN_NAME);
            long migrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            artistRepository.findFirstByTypeAndName(artistType, artistName).ifPresentOrElse(
                    artist -> {
                        artist.setMigrationId(migrationId);
                        migratedArtists.put(migrationId, artist);
                    },
                    () -> {
                        Artist artist = new Artist();
                        artist.setType(artistType);
                        artist.setName(artistName);
                        artist.setMigrationId(migrationId);

                        artistRepository.save(artist);
                        migratedArtists.put(migrationId, artist);
                        counter.getAndIncrement();
                    });
        }
        return counter.get();
    }

    private int importArtistDetails(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLIST_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long artistMigrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            String biography = row.getString(NOTES_COLUMN_NAME);
            Artist artist;

            if ((biography != null) && ((artist = migratedArtists.get(artistMigrationId)) != null)) {
                artistDetailRepository.findArtistDetailByArtist(artist).orElseGet(() -> {
                    ArtistDetail artistDetail = new ArtistDetail();
                    artistDetail.setArtist(artist);
                    artistDetail.setBiography(biography);

                    artistDetailRepository.save(artistDetail);
                    counter.getAndIncrement();

                    return artistDetail;
                });
            }
        }

        return counter.get();
    }

    private int importArtistCategories(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLISTCAT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long artistMigrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            Artist artist = migratedArtists.get(artistMigrationId);

            if (artist != null) {
                long migrationId = row.getInt(ARTISTLISTCAT_ID_COLUMN_NAME).longValue();
                Short catId = row.getShort(CAT_ID_COLUMN_NAME);
                String title = row.getString(TITLE_COLUMN_NAME);

                if (catId != null && title != null) {
                    artistCategoryRepository.findFirstByMigrationId(migrationId).orElseGet(() -> {
                        ArtistCategory artistCategory = new ArtistCategory();
                        artistCategory.setArtist(artist);
                        artistCategory.setType(row.getShort(CAT_ID_COLUMN_NAME) == 0 ? ArtistCategoryType.GENRE : ArtistCategoryType.STYLE);
                        artistCategory.setName(cleanseArtistCategory(title));
                        artistCategory.setMigrationId(migrationId);

                        artistCategoryRepository.save(artistCategory);
                        counter.getAndIncrement();

                        return artistCategory;
                    });
                }
            }
        }

        return counter.get();
    }

    private String cleanseArtistCategory(String title) {
        return title.replace("/ ", "/");
    }

    private int importArtistLyrics(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(ARTISTLYRICS_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            long artistMigrationId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
            Artist artist = migratedArtists.get(artistMigrationId);

            if (artist != null) {
                String songName = row.getString(SONGNAME_COLUMN_NAME);
                String lyricsText = row.getString(LYRICSTEXT_COLUMN_NAME);

                if (songName != null && lyricsText != null) {
                    artistLyricsRepository.findFirstByArtistAndTitle(artist, songName).ifPresentOrElse(
                            artistLyrics -> {
                                artistLyrics.setText(lyricsText);
                                artistLyricsRepository.save(artistLyrics);
                            },
                            () -> {
                                ArtistLyrics artistLyrics = new ArtistLyrics();
                                artistLyrics.setArtist(artist);
                                artistLyrics.setTitle(songName);
                                artistLyrics.setText(lyricsText);

                                artistLyricsRepository.save(artistLyrics);
                                counter.getAndIncrement();
                            }
                    );
                }
            }
        }

        return counter.get();
    }
}
