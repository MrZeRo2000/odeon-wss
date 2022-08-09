package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.*;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class ClassicsMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int ROW_ID = 1199;

    private static final Logger logger = LoggerFactory.getLogger(ClassicsMDBImportProcessor.class);

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private Map<Long, Artist> migratedArtists;
    private Map<String, Artifact> migratedArtifacts;

    public ClassicsMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository)
    {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
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
    }

    private int importArtifacts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(CLASSICS_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);
        migratedArtifacts = new HashMap<>();

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == ROW_ID) {
                String artifactName = row.getString(DIR_NAME_COLUMN_NAME);
                if (!migratedArtifacts.containsKey(artifactName)) {
                    Long migrationArtistId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
                    Artist artist = migratedArtists.get(migrationArtistId);

                    Long migrationPerformerArtistId = row.getInt(PERFORMER_ARTISTLIST_ID_COLUMN_NAME).longValue();
                    Artist performerArtist = migratedArtists.get(migrationPerformerArtistId);

                    Integer yearData = row.getInt(YEAR_COLUMN_NAME);
                    Long year = yearData == null ? null : yearData.longValue();

                    Artifact artifact = new Artifact();
                    artifact.setArtifactType(ArtifactType.withClassics());
                    artifact.setTitle(artifactName);
                    artifact.setArtist(artist);
                    artifact.setPerformerArtist(performerArtist);
                    artifact.setYear(year);

                    artifactRepository.save(artifact);
                    migratedArtifacts.put(artifactName, artifact);
                }
            }
        }


        return counter.get();
    }
}
