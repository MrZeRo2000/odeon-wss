package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.repository.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVMusicMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int DV_MUSIC_REC_ID = 1253;

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    public DVMusicMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository
    ) {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
    }

    public int importArtifacts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVCONT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == DV_MUSIC_REC_ID) {
                String artifactName = row.getString(FILE_NAME_COLUMN_NAME);

                Long migrationArtistId = row.getInt(ARTISTLIST_ID_COLUMN_NAME).longValue();
                Artist artist = artistRepository.findFirstByMigrationId(migrationArtistId).orElseThrow(
                        () -> new ProcessorException(String.format("Unable to find artist by migrationId: {%d}", migrationArtistId))
                );
                Long year = row.getInt(YEAR_COLUMN_NAME) == null ? null : row.getInt(YEAR_COLUMN_NAME).longValue();

                Artifact artifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                        ArtifactType.withDVMusic(),
                        artist,
                        artifactName,
                        year
                ).orElseGet(() -> {
                    Artifact newArtifact = new Artifact();

                    newArtifact.setArtifactType(ArtifactType.withDVMusic());
                    newArtifact.setArtist(artist);
                    newArtifact.setTitle(artifactName);
                    newArtifact.setYear(year);

                    return newArtifact;
                });

                artifactRepository.save(artifact);
                counter.getAndIncrement();
            }
        }

        return counter.get();
    }
}
