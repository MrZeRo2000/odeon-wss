package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.dto.CompositionValidationDTO;
import com.romanpulov.odeonwss.dto.MediaFileValidationDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class AbstractValidateProcessor extends AbstractFileSystemProcessor {

    protected boolean compareStringSets(
            Set<String> pathStrings,
            Set<String> dbStrings,
            String pathErrorMessage,
            String dbErrorMessage)
    {
        boolean result = true;

        Set<String> pathStringsDiff = new HashSet<>(pathStrings);
        pathStringsDiff.removeAll(dbStrings);
        if (!pathStringsDiff.isEmpty()) {
            errorHandler(dbErrorMessage, String.join(",", pathStringsDiff));
            result = false;
        }

        Set<String> dbStringsDiff = new HashSet<>(dbStrings);
        dbStringsDiff.removeAll(pathStrings);
        if (!dbStringsDiff.isEmpty()) {
            errorHandler(pathErrorMessage, String.join(",", dbStringsDiff));
            result = false;
        }

        return result;

    }

    protected List<MediaFileValidationDTO> loadFromPath(Path path) throws ProcessorException {
        List<MediaFileValidationDTO> result = new ArrayList<>();

        try (Stream<Path> artistPathStream = Files.list(path)){
            for (Path artistPath: artistPathStream.collect(Collectors.toList())) {
                if (!Files.isDirectory(artistPath)) {
                    errorHandler(ProcessorMessages.ERROR_EXPECTED_DIRECTORY, artistPath.getFileName().toString());
                } else {
                    loadFromArtistPath(artistPath, result);
                }
            }
        } catch (IOException | ProcessorException e) {
            if (e instanceof ProcessorException) {
                throw (ProcessorException) e;
            } else {
                throw new ProcessorException("Exception:" + e.getMessage());
            }
        }

        return result;
    }

    abstract protected void loadFromArtistPath(Path artistPath, List<MediaFileValidationDTO> result)
            throws ProcessorException;

    protected boolean validateArtistNames(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {

        Set<String> pathArtistNames = pathValidation.stream().map(CompositionValidationDTO::getArtistName).collect(Collectors.toSet());
        Set<String> dbArtistNames = dbValidation.stream().map(CompositionValidationDTO::getArtistName).collect(Collectors.toSet());

        return compareStringSets(
                pathArtistNames,
                dbArtistNames,
                ProcessorMessages.ERROR_ARTISTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTISTS_NOT_IN_DB
        );
    }

    protected boolean validateArtifacts(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathArtifacts = pathValidation.stream()
                .map(d -> d.getArtistName() +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()))
                .collect(Collectors.toSet());
        Set<String> dbArtifacts = dbValidation.stream()
                .map(d -> d.getArtistName() +
                        ProcessorMessages.FORMAT_PATH_DELIMITER +
                        NamesParser.formatMusicArtifact(d.getArtifactYear(), d.getArtifactTitle()))
                .collect(Collectors.toSet());

        return compareStringSets(
                pathArtifacts,
                dbArtifacts,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_FILES,
                ProcessorMessages.ERROR_ARTIFACTS_NOT_IN_DB
        );
    }

    protected boolean validateCompositions(List<MediaFileValidationDTO> pathValidation, List<MediaFileValidationDTO> dbValidation) {
        Set<String> pathCompositions = MediaFileValidationDTO.getCompositions(pathValidation);
        Set<String> dbCompositions = MediaFileValidationDTO.getCompositions(dbValidation);

        return compareStringSets(
                pathCompositions,
                dbCompositions,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_FILES,
                ProcessorMessages.ERROR_COMPOSITIONS_NOT_IN_DB
        );
    }


}
