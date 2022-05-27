package com.romanpulov.odeonwss.service.processor;

public class ProcessorMessages {
    public static final String INFO_STARTED = "Started %s";

    static final String ERROR_ROOT_FOLDER_NOT_FOUND = "Root folder not found";
    static final String ERROR_PATH_NOT_FOUND = "Path not found: %s";
    static final String ERROR_EXCEPTION = "Exception: %s";
    static final String ERROR_EXPECTED_DIRECTORY = "Expected directory, found: %s";
    static final String ERROR_EXPECTED_FILE = "Expected file, found: %s";
    static final String ERROR_ARTIST_NOT_FOUND = "Artist %s not found";
    static final String ERROR_PARSING_ARTIFACT_NAME = "Error parsing artifact name: %s";
    static final String ERROR_PARSING_COMPOSITION_NAME = "Error parsing composition name: %s";
    static final String ERROR_PARSING_FILE = "Error parsing file: %s";
    static final String ERROR_PROCESSING_FILES = "Error processing files: %s";
    static final String ERROR_WRONG_FILE_TYPE = "Wrong file type: %s";
    static final String ERROR_NO_DATA_FOR_FILE = "No data for file: %s";

    static final String ERROR_ARTISTS_NOT_IN_DB = "Artists not in database: %s";
    static final String ERROR_ARTISTS_NOT_IN_FILES = "Artists not in files: %s";
    static final String INFO_ARTISTS_VALIDATED = "Artists validated";

    static final String ERROR_ARTIFACTS_NOT_IN_DB = "Artifacts not in database: %s";
    static final String ERROR_ARTIFACTS_NOT_IN_FILES = "Artifacts not in files: %s";
    static final String INFO_ARTIFACTS_VALIDATED = "Artifacts validated";

    static final String ERROR_COMPOSITIONS_NOT_IN_DB = "Compositions not in database: %s";
    static final String ERROR_COMPOSITIONS_NOT_IN_FILES = "Compositions not in files: %s";
    static final String INFO_COMPOSITIONS_VALIDATED = "Compositions validated";

    static final String FORMAT_PATH_DELIMITER = " >> ";

}