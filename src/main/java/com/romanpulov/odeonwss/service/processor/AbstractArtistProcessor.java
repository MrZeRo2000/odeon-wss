package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import com.romanpulov.odeonwss.repository.MediaFileRepository;
import com.romanpulov.odeonwss.service.processor.parser.NamesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractArtistProcessor extends AbstractFileSystemProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractArtistProcessor.class);

    protected final ArtistRepository artistRepository;

    protected final ArtifactRepository artifactRepository;

    protected final MediaFileRepository mediaFileRepository;

    public AbstractArtistProcessor(
            ArtistRepository artistRepository,
            ArtifactRepository artifactRepository,
            MediaFileRepository mediaFileRepository) {
        this.artistRepository = artistRepository;
        this.artifactRepository = artifactRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    protected abstract ArtifactType getArtifactType();

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        List<Pair<Path, Artist>> pathArtists = processArtists(path);
        infoHandler(ProcessorMessages.INFO_ARTISTS_LOADED, pathArtists.size());

        if (!pathArtists.isEmpty()) {
            List<Pair<Path, Artifact>> pathArtifacts = processArtifacts(pathArtists);
            infoHandler(ProcessorMessages.INFO_ARTIFACTS_LOADED, pathArtifacts.size());

            List<Pair<Path, Artifact>> processPathArtifacts = preparePathArtifacts(path);

            if (!processPathArtifacts.isEmpty()) {
                infoHandler(ProcessorMessages.INFO_TRACKS_LOADED, processTracks(processPathArtifacts));
            }
        }
    }

    protected List<Pair<Path, Artist>> processArtists(Path path) throws ProcessorException {
        List<Pair<Path, Artist>> result = new ArrayList<>();

        List<Path> artistFiles = new ArrayList<>();
        if (!PathReader.readPathFoldersOnly(this, path, artistFiles)) {
            return result;
        }

        AtomicInteger counter = new AtomicInteger(0);

        for (Path p: artistFiles) {
            String artistName = p.getFileName().toString();
            processingEventHandler(ProcessorMessages.PROCESSING_ARTIST, artistName);
            logger.debug(String.format("processArtists artistName=%s", artistName));

            Optional<Artist> artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName);
            if (artist.isEmpty()) {
                warningHandlerWithAddArtistAction(String.format(ProcessorMessages.ERROR_ARTIST_NOT_FOUND, artistName), artistName);
            } else {
                result.add(Pair.of(p, artist.get()));
                counter.getAndIncrement();
            }
        }

        return result;
    }

    protected List<Pair<Path, Artifact>> processArtifacts(List<Pair<Path, Artist>> pathArtists) throws ProcessorException {
        List<Pair<Path, Artifact>> result = new ArrayList<>();

        // load artifact folders to flat list
        List<Pair<Path, Pair<Artist, NamesParser.YearTitle>>> flatPathArtists = new ArrayList<>();
        for (Pair<Path, Artist> pathArtistPair: pathArtists) {
            logger.debug(String.format("processArtifacts path=%s", pathArtistPair.getFirst()));

            List<Path> artifactFiles = new ArrayList<>();
            if (!PathReader.readPathFoldersOnly(this, pathArtistPair.getFirst(), artifactFiles)) {
                return result;
            }

            for (Path p: artifactFiles) {
                String artifactName = p.getFileName().toString();
                processingEventHandler(ProcessorMessages.PROCESSING_ARTIFACT, p.getFileName().toString());
                logger.debug(String.format("processArtifacts artifactName=%s", artifactName));

                NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactName);
                if (yt == null) {
                    errorHandlerItem(
                            ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME,
                            String.format(
                                    MediaFileValidator.DELIMITER_FORMAT,
                                    pathArtistPair.getFirst().getFileName(),
                                    p.toAbsolutePath().getFileName().toString()));
                    return result;
                }
                flatPathArtists.add(Pair.of(p, Pair.of(pathArtistPair.getSecond(), yt)));
            }
        }

        // process flat list
        for (Pair<Path, Pair<Artist, NamesParser.YearTitle>> pathArtistPair: flatPathArtists) {
            Artifact artifact = new Artifact();
            artifact.setArtifactType(getArtifactType());
            artifact.setArtist(pathArtistPair.getSecond().getFirst());
            artifact.setTitle(pathArtistPair.getSecond().getSecond().title());
            artifact.setYear((long) pathArtistPair.getSecond().getSecond().year());

            Optional<Artifact> existingArtifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                    artifact.getArtifactType(),
                    artifact.getArtist(),
                    artifact.getTitle(),
                    artifact.getYear()
            );

            if (existingArtifact.isEmpty()) {
                processingEventHandler(ProcessorMessages.PROCESSING_SAVING_ARTIFACT, pathArtistPair.getFirst().toString());
                artifactRepository.save(artifact);
                result.add(Pair.of(pathArtistPair.getFirst(), artifact));
            }
        }

        return result;
    }

    public List<Pair<Path, Artifact>> preparePathArtifacts(Path rootPath) {
        List<Pair<Path, Artifact>> result = new ArrayList<>();

        List<Artifact> artifacts = artifactRepository.getAllByArtifactTypeWithoutTracks(this.getArtifactType());
        for (Artifact artifact: artifacts) {
            logger.debug("Got artifact without tracks:" + artifact);

            //remove media files
            processingEventHandler(ProcessorMessages.PROCESSING_PREPARING_ARTIFACT, artifact.getTitle());
            mediaFileRepository.deleteAllByArtifact(artifact);
            logger.debug("Deleted media files by artifact");

            Artist artist = artifact.getArtist();
            Long year = artifact.getYear();
            if ((artist != null) && (year != null)) {
                Path path = Path.of(
                        rootPath.toString(),
                        artist.getName(),
                        NamesParser.formatMusicArtifact(year, artifact.getTitle()));
                if (Files.exists(path)) {
                    result.add(Pair.of(path, artifact));
                } else {
                    logger.debug("Expected path does not exist:" + path);
                }
            }
        }

        return result;
    }

    protected abstract int processTracks(List<Pair<Path, Artifact>> pathArtifacts) throws ProcessorException;
}
