package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.entity.Artist;
import com.romanpulov.odeonwss.entity.ArtistTypes;
import com.romanpulov.odeonwss.repository.ArtistRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoadMP3Processor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LoadMP3Processor.class);

    private ArtistRepository artistRepository;

    public LoadMP3Processor(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = Path.of(Optional.ofNullable(
                rootFolder).orElseThrow(() -> new ProcessorException("MP3 file not specified")
        ));

        if (Files.notExists(path)) {
            throw new ProcessorException("Path not found:" + path);
        }

        try {
            for (Path p : Files.list(path).collect(Collectors.toList())) {
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

        Artist artist = artistRepository.findFirstByTypeAndName(ArtistTypes.A.name(), artistName);
        if (artist == null) {
            warningHandlerWithAddArtistAction(String.format("Artist %s not found", artistName), artistName);
            return;
        }

        try {
            for (Path p: Files.list(path).collect(Collectors.toList())) {
                logger.debug("File:" + p.getFileName());
                processArtifactsPath(p);
            }
        } catch (IOException e) {
            throw new ProcessorException("Error processing files: " + e.getMessage());
        }
    }

    private void processArtifactsPath(Path path) throws ProcessorException {
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

        try {
            for (Path p: Files.list(path).collect(Collectors.toList())) {
                logger.debug("File:" + p.getFileName());
                processCompositionPath(p);
            }
        } catch (IOException e) {
            throw new ProcessorException("Error processing files: " + e.getMessage());
        }
    }

    private void processCompositionPath(Path path) throws ProcessorException {
        if (Files.isDirectory(path)) {
            errorHandler("Expected file, found: " + path.toAbsolutePath());
            return;
        }

        String compositionName = path.getFileName().toString();
        if (compositionName.endsWith("mp3")) {
            errorHandler("Wrong file type: " + path.toAbsolutePath());
        }

        NamesParser.NumberTitle nt = NamesParser.parseMusicComposition(compositionName);
    }

}
