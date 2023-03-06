package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.*;
import com.romanpulov.odeonwss.repository.*;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ClassicsMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int ROW_ID = 1199;

    private static final String ARTIST_CATEGORY_CLASSICAL = "Classical";

    private static final String VARIOUS_COMPOSERS_ARTIST_NAME = "Various Composers";

    private static final String VARIOUS_PERFORMERS_ARTIST_NAME = "Various Performers";

    private static final Map<Long, String> RENAME_ARTISTS_MAP = new HashMap<>();
    static {
        RENAME_ARTISTS_MAP.put(1921L, VARIOUS_COMPOSERS_ARTIST_NAME);
    }

    private static final Logger logger = LoggerFactory.getLogger(ClassicsMDBImportProcessor.class);

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final ArtifactRepository artifactRepository;

    private final TrackRepository trackRepository;

    private Map<Long, Artist> migratedArtists;

    public ClassicsMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtifactRepository artifactRepository,
            TrackRepository trackRepository)
    {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.artifactRepository = artifactRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        ArtistsMDBImporter artistsMDBImporter = new ArtistsMDBImporter(
                artistRepository,
                artistDetailRepository,
                artistCategoryRepository,
                artistLyricsRepository,
                mdbReader,
                ArtistType.CLASSICS
        );

        infoHandler(ProcessorMessages.INFO_ARTISTS_LOADED, artistsMDBImporter.importArtists());
        infoHandler(ProcessorMessages.INFO_ARTIST_DETAILS_IMPORTED, artistsMDBImporter.importArtistDetails());
        infoHandler(ProcessorMessages.INFO_ARTIST_CATEGORIES_IMPORTED, artistsMDBImporter.importArtistCategories());

        this.migratedArtists = artistsMDBImporter.getMigratedArtists();

        infoHandler(ProcessorMessages.INFO_ARTISTS_CLEANSED, cleanseArtistNames());

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifactsAndTracks(mdbReader));
    }

    private static class ClassicsData {
        Artifact artifact;
        final List<Track> tracks = new ArrayList<>();

        public static ClassicsData withArtifact(Artifact artifact) {
            ClassicsData instance = new ClassicsData();
            instance.artifact = artifact;
            return instance;
        }
    }

    private void saveArtistClassicsGenreAndCategory(Artist artist) {
        ArtistCategory genreArtistCategory = new ArtistCategory();
        genreArtistCategory.setArtist(artist);
        genreArtistCategory.setType(ArtistCategoryType.GENRE);
        genreArtistCategory.setName(ARTIST_CATEGORY_CLASSICAL);
        artistCategoryRepository.save(genreArtistCategory);

        ArtistCategory styleArtistCategory = new ArtistCategory();
        styleArtistCategory.setArtist(artist);
        styleArtistCategory.setType(ArtistCategoryType.STYLE);
        styleArtistCategory.setName(ARTIST_CATEGORY_CLASSICAL);
        artistCategoryRepository.save(styleArtistCategory);
    }


    private int cleanseArtistNames() {
        AtomicInteger counter = new AtomicInteger(0);

        RENAME_ARTISTS_MAP.forEach((key, value) -> {
            Artist artist = migratedArtists.get(key);
            if ((artist != null) && (!artist.getName().equals(value))) {
                artist.setName(value);
                artistRepository.save(artist);
                counter.getAndIncrement();

                saveArtistClassicsGenreAndCategory(artist);
            }
        });

        artistRepository.findFirstByTypeAndName(ArtistType.CLASSICS, VARIOUS_PERFORMERS_ARTIST_NAME)
                .orElseGet(() -> {
                    Artist variousPerformerArtist = new Artist();
                    variousPerformerArtist.setType(ArtistType.CLASSICS);
                    variousPerformerArtist.setName(VARIOUS_PERFORMERS_ARTIST_NAME);
                    artistRepository.save(variousPerformerArtist);
                    counter.getAndIncrement();

                    saveArtistClassicsGenreAndCategory(variousPerformerArtist);

                    return variousPerformerArtist;
                });

        return counter.get();
    }

    @Transactional
    public int importArtifactsAndTracks(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(CLASSICS_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        Map<String, ClassicsData> classicsDataMap = new HashMap<>();
        Artist variousComposersArtist = artistRepository
                .findFirstByTypeAndName(ArtistType.CLASSICS, VARIOUS_COMPOSERS_ARTIST_NAME)
                .orElseThrow(() -> new ProcessorException("Various composers artist not found"));
        Artist variousPerformersArtist = artistRepository
                .findFirstByTypeAndName(ArtistType.CLASSICS, VARIOUS_PERFORMERS_ARTIST_NAME)
                .orElseThrow(() -> new ProcessorException("Various performers artist not found"));

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == ROW_ID) {
                String artifactName = row.getString(DIR_NAME_COLUMN_NAME);

                Long migrationArtistId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
                Artist artist = migratedArtists.get(migrationArtistId);

                Long migrationPerformerArtistId = row.getInt(PERFORMER_ARTISTLIST_ID_COLUMN_NAME).longValue();
                Artist performerArtist = migratedArtists.get(migrationPerformerArtistId);

                ClassicsData classicsData = classicsDataMap.get(artifactName);
                if (classicsData == null) {
                    Artifact artifact = artifactRepository
                            .getArtifactWithArtistByTitle(artifactName)
                            .orElseGet(() -> {
                                Artifact newArtifact = new Artifact();

                                newArtifact.setArtifactType(ArtifactType.withMP3());
                                newArtifact.setTitle(artifactName);
                                newArtifact.setArtist(artist);
                                newArtifact.setPerformerArtist(performerArtist);

                                Integer yearData = row.getInt(YEAR_COLUMN_NAME);
                                Long year = yearData == null ? null : yearData.longValue();
                                newArtifact.setYear(year);

                                artifactRepository.save(newArtifact);
                                counter.getAndIncrement();

                                return newArtifact;
                            });

                    classicsDataMap.put(artifactName, (classicsData = ClassicsData.withArtifact(artifact)));
                }
                String trackTitle = row.getString(TITLE_COLUMN_NAME);
                Track track = trackRepository
                        .findTrackByArtifactAndTitle(classicsData.artifact, trackTitle)
                        .orElseGet(() -> {
                            Track newTrack = new Track();

                            newTrack.setArtifact(classicsDataMap.get(artifactName).artifact);
                            newTrack.setArtist(artist);
                            newTrack.setPerformerArtist(performerArtist);
                            newTrack.setTitle(row.getString(TITLE_COLUMN_NAME));

                            newTrack.setDiskNum(1L);
                            newTrack.setNum(classicsDataMap.get(artifactName).tracks.size() + 1L);

                            trackRepository.save(newTrack);

                            return newTrack;
                        });

                logger.debug("Processing " + artist);
                if (
                        (artist != null) &&
                        (classicsData.artifact.getArtist() != null) &&
                        !(artist.getName().equals(classicsData.artifact.getArtist().getName())) &&
                        !(classicsData.artifact.getArtist().getName().equals(VARIOUS_COMPOSERS_ARTIST_NAME))
                ) {
                    classicsData.artifact.setArtist(variousComposersArtist);
                    artifactRepository.save(classicsData.artifact);
                }

                if (
                        ((performerArtist == null) && (classicsData.artifact.getPerformerArtist() != null)) ||
                        ((performerArtist != null) && (classicsData.artifact.getPerformerArtist() == null)) ||
                        ((performerArtist != null) && (classicsData.artifact.getPerformerArtist() != null) &&
                        !(performerArtist.getName().equals(classicsData.artifact.getPerformerArtist().getName())) &&
                        !(classicsData.artifact.getPerformerArtist().getName().equals(VARIOUS_PERFORMERS_ARTIST_NAME)))
                ) {
                    classicsData.artifact.setPerformerArtist(variousPerformersArtist);
                    artifactRepository.save(classicsData.artifact);
                }

                classicsData.tracks.add(track);
            }
        }

        return counter.get();
    }
}
