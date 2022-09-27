package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Table;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.romanpulov.odeonwss.service.processor.MDBConst.DVCONT_TABLE_NAME;

@Component
public class DVMusicMDBImportProcessor extends AbstractMDBImportProcessor {
    private static final int DV_MUSIC_REC_ID = 1253;

    private final ArtistRepository artistRepository;

    private final ArtistDetailRepository artistDetailRepository;

    private final ArtistCategoryRepository artistCategoryRepository;

    private final ArtistLyricsRepository artistLyricsRepository;

    private final ArtifactRepository artifactRepository;

    private final CompositionRepository compositionRepository;

    private final ArtistsMDBImportProcessor artistsMDBImportProcessor;

    public DVMusicMDBImportProcessor(
            ArtistRepository artistRepository,
            ArtistDetailRepository artistDetailRepository,
            ArtistCategoryRepository artistCategoryRepository,
            ArtistLyricsRepository artistLyricsRepository,
            ArtifactRepository artifactRepository,
            CompositionRepository compositionRepository,
            ArtistsMDBImportProcessor artistsMDBImportProcessor
    ) {
        this.artistRepository = artistRepository;
        this.artistDetailRepository = artistDetailRepository;
        this.artistCategoryRepository = artistCategoryRepository;
        this.artistLyricsRepository = artistLyricsRepository;
        this.artifactRepository = artifactRepository;
        this.compositionRepository = compositionRepository;
        this.artistsMDBImportProcessor = artistsMDBImportProcessor;
    }

    @Override
    protected void importMDB(MDBReader mdbReader) throws ProcessorException {
        artistsMDBImportProcessor.setProgressHandler(this.getProgressHandler());
        artistsMDBImportProcessor.importMDB(mdbReader);

        infoHandler(ProcessorMessages.INFO_ARTIFACTS_IMPORTED, importArtifacts(mdbReader));
    }

    public int importArtifacts(MDBReader mdbReader) throws ProcessorException {
        Table table = mdbReader.getTable(DVCONT_TABLE_NAME);
        AtomicInteger counter = new AtomicInteger(0);

        return counter.get();
    }
}
