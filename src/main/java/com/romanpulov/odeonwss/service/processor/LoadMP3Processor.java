package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artifact;
import com.romanpulov.odeonwss.entity.ArtifactType;
import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistTypes;
import com.romanpulov.odeonwss.repository.ArtifactRepository;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoadMP3Processor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LoadMP3Processor.class);

    private final ArtistRepository artistRepository;

    private final ArtifactRepository artifactRepository;

    public LoadMP3Processor(ArtistRepository artistRepository, ArtifactRepository artifactRepository) {
        this.artistRepository = artistRepository;
        this.artifactRepository = artifactRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = Path.of(Optional.ofNullable(
                rootFolder).orElseThrow(() -> new ProcessorException("MP3 file not specified")
        ));

        if (Files.notExists(path)) {
            throw new ProcessorException("Path not found:" + path);
        }

        try (Stream<Path> stream = Files.list(path)){
            for (Path p : stream.collect(Collectors.toList())) {
                logger.debug("Path:" + p.getFileName());
                processArtistsPath(p);
            }
        } catch (IOException e) {
            throw new ProcessorException("Exception:" + e.getMessage());
        }
    }

    private void processArtistsPath(Path path) throws ProcessorException {
        if (!Files.isDirectory(path)) {
            errorHandler("Expected directory, found " + path.getFileName());
            return;
        }

        String artistName = path.getFileName().toString();

        Optional<Artist> artist = artistRepository.findFirstByTypeAndName(ArtistTypes.A.name(), artistName);
        if (artist.isEmpty()) {
            warningHandlerWithAddArtistAction(String.format("Artist %s not found", artistName), artistName);
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
            errorHandler("Expected directory, found " + path.getFileName());
            return;
        }

        String artifactName = path.getFileName().toString();

        NamesParser.YearTitle yt = NamesParser.parseMusicArtifactTitle(artifactName);
        if (yt == null) {
            errorHandler("Error parsing artifact name:" + path.toAbsolutePath().getFileName());
            return;
        }

        Artifact artifact = new Artifact();
        artifact.setArtifactType(ArtifactType.withMP3());
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

            try (Stream<Path> stream = Files.list(path)) {
                for (Path p : stream.collect(Collectors.toList())) {
                    logger.debug("File:" + p.getFileName());
                    processCompositionPath(p, artifact);
                }
            } catch (IOException e) {
                throw new ProcessorException("Error processing files: " + e.getMessage());
            }
        }
    }

    private void processCompositionPath(Path path, Artifact artifact) throws ProcessorException {
        if (Files.isDirectory(path)) {
            errorHandler("Expected file, found: " + path.toAbsolutePath());
            return;
        }

        String compositionName = path.getFileName().toString();
        if (!compositionName.endsWith("mp3")) {
            errorHandler("Wrong file type: " + path.toAbsolutePath());
        }

        NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionName);
        if (nt == null) {
            errorHandler("Error parsing composition:" + path.toAbsolutePath().getFileName());
            return;
        }
    }

}
