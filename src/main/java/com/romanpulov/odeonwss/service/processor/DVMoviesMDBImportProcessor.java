package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Composition;
import com.romanpulov.odeonwss.entity.DVType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.CompositionRepository;
import com.romanpulov.odeonwss.repository.DVTypeRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.romanpulov.odeonwss.service.processor.MDBConst.*;

@Component
public class DVMoviesMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int DV_MOVIE_REC_ID = 1254;

    private final DVTypeRepository dvTypeRepository;

    private final DVProductMDBImportProcessor dvProductMDBImportProcessor;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private Map<Long, DVType> dvTypeMap;

    private Map<Long, Artifact> artifacts;

    public DVMoviesMDBImportProcessor(
            DVTypeRepository dvTypeRepository,
            DVProductMDBImportProcessor dvProductMDBImportProcessor,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository
    ) {
        this.dvTypeRepository = dvTypeRepository;
        this.dvProductMDBImportProcessor = dvProductMDBImportProcessor;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        if (dvTypeMap == null) {
            dvTypeMap = dvTypeRepository.findAllMap();
        }
        artifacts = new HashMap<>();

        dvProductMDBImportProcessor.setProgressHandler(getProgressHandler());
        dvProductMDBImportProcessor.setRootFolder(getRootFolder());
        dvProductMDBImportProcessor.execute();

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
        infoHandler(ProcessorMessages.INFO_COMPOSITIONS_IMPORTED, importCompositions(mdbReader));
        infoHandler(ProcessorMessages.INFO_MEDIA_FILES_IMPORTED, importMediaFiles(mdbReader));
    }

    public int importArtifacts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVCONT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        ArtifactType artifactType =ArtifactType.withDVMovies();
        Map<Long, Artifact> migrationArtifacts =
                artifactRepository.findAllByArtifactTypeMigrationIdMap(artifactType);

        for (Row row: table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == DV_MOVIE_REC_ID) {
                long id = row.getInt(DVCONT_ID_COLUMN_NAME);
                String artifactName = row.getString(FILE_NAME_COLUMN_NAME);

                Artifact artifact = migrationArtifacts.get(id);
                if (artifact == null) {
                    artifact = new Artifact();
                    artifact.setArtifactType(artifactType);
                    artifact.setDuration(0L);
                    artifact.setTitle(artifactName);
                    artifact.setMigrationId(id);

                    artifactRepository.save(artifact);

                    counter.getAndIncrement();
                }

                this.artifacts.put(id, artifact);
            }
        }

        return counter.get();
    }

    public int importCompositions(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        ArtifactType artifactType = ArtifactType.withDVMovies();
        Map<String, Composition> migrationCompositions =
                compositionRepository.getCompositionsByArtifactType(artifactType)
                        .stream()
                        .collect(Collectors.toMap(Composition::getTitle, v-> v));

        for (Row row: table) {
            long id = row.getInt(DVDET_ID_COLUMN_NAME);
            long contId = row.getInt(DVCONT_ID_COLUMN_NAME);

            if (this.artifacts.containsKey(contId)) {
                String title = row.getString(TITLE_COLUMN_NAME);
                Composition composition = migrationCompositions.get(title);
                if (composition == null) {
                    composition = new Composition();
                    composition.setArtifact(this.artifacts.get(contId));
                    composition.setTitle(row.getString(TITLE_COLUMN_NAME));
                    composition.setDuration(0L);
                    composition.setDvType(dvTypeMap.get(row.getInt(VIDEOTYPE_ID_COLUMN_NAME).longValue()));
                    composition.setMigrationId(id);

                    compositionRepository.save(composition);
                    migrationCompositions.put(title, composition);

                    counter.getAndIncrement();
                }
            }
        }

        return counter.get();
    }

    public int importMediaFiles(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVDET_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        return counter.get();
    }
}
