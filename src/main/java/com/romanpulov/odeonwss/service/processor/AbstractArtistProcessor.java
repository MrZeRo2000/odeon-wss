package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistType;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractArtistProcessor extends AbstractFileSystemProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractArtistProcessor.class);

    protected final ArtistRepository artistRepository;

    protected final ArtifactRepository artifactRepository;

    protected static class CompositionsSummary {
        protected long duration;
        protected long size;
    }

    public AbstractArtistProcessor(ArtistRepository artistRepository, ArtifactRepository artifactRepository) {
        this.artistRepository = artistRepository;
        this.artifactRepository = artifactRepository;
    }

    protected abstract ArtifactType getArtifactType();

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        try (Stream<Path> stream = Files.list(path)){
            for (Path p : stream.collect(Collectors.toList())) {
                logger.debug("Path:" + p.getFileName());
                processArtistsPath(p);
            }
        } catch (IOException e) {
            throw new ProcessorException(ProcessorMessages.ERROR_EXCEPTION,  e.getMessage());
        }
    }

    protected void processArtistsPath(Path path) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
            return;
        }

        String artistName = path.getFileName().toString();

        Optional<Artist> artist = artistRepository.findFirstByTypeAndName(ArtistType.ARTIST, artistName);
        if (artist.isEmpty()) {
            warningHandlerWithAddArtistAction(String.format(ProcessorMessages.ERROR_ARTIST_NOT_FOUND, artistName), artistName);
            return;
        }

        try (Stream<Path> stream = Files.list(path)){
            for (Path p: stream.collect(Collectors.toList())) {
                logger.debug("File:" + p.getFileName());
                processArtifactsPath(p, artist.get());
            }
        } catch (IOException e) {
            throw new ProcessorException("Error processing files: " + e.getMessage());
        }
    }

    private void processArtifactsPath(Path path, Artist artist) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, path.getFileName());
            return;
        }

        String artifactName = path.getFileName().toString();

        NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactName);
        if (yt == null) {
            errorHandler(ProcessorMessages.ERROR_PARSING_ARTIFACT_NAME, path.toAbsolutePath().getFileName());
            return;
        }

        Artifact artifact = new Artifact();
        artifact.setArtifactType(getArtifactType());
        artifact.setArtist(artist);
        artifact.setTitle(yt.getTitle());
        artifact.setYear((long) yt.getYear());

        Optional<Artifact> existingArtifact = artifactRepository.findFirstByArtifactTypeAndArtistAndTitleAndYear(
                artifact.getArtifactType(),
                artifact.getArtist(),
                artifact.getTitle(),
                artifact.getYear()
        );

        if (existingArtifact.isEmpty()) {
            artifactRepository.save(artifact);

            processCompositionsPath(path, artifact);
        }
    }

    protected abstract void processCompositionsPath(Path path, Artifact artifact) throws ProcessorException;
}
