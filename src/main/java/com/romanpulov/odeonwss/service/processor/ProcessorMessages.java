package com.romanpulov.odeonwss.service.processor;

public class ProcessorMessages {
    public static final String INFO_STARTED = "Started %s";
    public static final String PROCESSING_COMPLETED = "Completed";

    static final String ERROR_ROOT_FOLDER_NOT_FOUND = "Root folder not found";
    static final String ERROR_PATH_NOT_FOUND = "Path not found: %s";
    static final String ERROR_FILE_NOT_FOUND = "File not found: %s";
    static final String ERROR_EXPECTED_DIRECTORY = "Expected directory, found: %s";
    static final String ERROR_EXPECTED_FILE = "Expected file, found: %s";
    static final String ERROR_ARTIST_NOT_FOUND = "Artist %s not found";
    static final String ERROR_PARSING_ARTIFACT_NAME = "Error parsing artifact name";
    static final String ERROR_PARSING_MUSIC_TRACK_NAME = "Error parsing music track name: %s";
    static final String ERROR_DUPLICATE_MUSIC_TRACK_NUMBER = "Duplicate track number for: %s";
    static final String ERROR_PARSING_FILE = "Error parsing file: %s";
    static final String ERROR_PROCESSING_FILES = "Error processing files: %s";
    static final String ERROR_WRONG_FILE_TYPE = "Wrong file type: %s";
    static final String ERROR_NO_DATA_FOR_FILE = "No data for file: %s";
    static final String ERROR_NO_DATA_FOR_FOLDER = "No data for folder: %s";
    static final String ERROR_FOLDER_WITH_DISK_NUMBERS_CONTAINS_OTHER = "Folder %s with disk numbers contains other items";
    static final String ERROR_FILES_IN_CUE_NOT_FOUND = "Files in Cue not found in %s";

    static final String ERROR_OPENING_MDB_DATABASE = "Error opening MDB database: %s";
    static final String ERROR_OPENING_MDB_TABLE = "Error opening MDB table %s: %s";
    static final String ERROR_PROCESSING_MDB_DATABASE = "Error processing MDB database: %s";

    static final String ERROR_ARTISTS_ARTIFACTS_TRACKS_NOT_IN_DB = "Artists not in database or have no artifacts and tracks";
    static final String ERROR_ARTISTS_ARTIFACTS_TRACKS_NOT_IN_FILES = "Artists not in files or have no artifacts and tracks";
    static final String INFO_ARTISTS_VALIDATED = "Artists validated";

    static final String INFO_ARTISTS_IMPORTED = "Artists imported";
    static final String INFO_ARTIST_DETAILS_IMPORTED = "Artist details imported";
    static final String INFO_ARTIST_CATEGORIES_IMPORTED = "Artist categories imported";
    static final String INFO_ARTIST_LYRICS_IMPORTED = "Artist lyrics imported";

    static final String ERROR_ARTIFACTS_NOT_IN_DB = "Artifacts not in database";
    static final String ERROR_ARTIFACTS_NOT_IN_FILES = "Artifacts not in files";
    static final String ERROR_ARTIFACTS_WITHOUT_ARTISTS = "Artifacts without artists";
    static final String INFO_ARTIFACTS_VALIDATED = "Artifacts validated";

    static final String ERROR_TRACKS_NOT_IN_DB = "Tracks not in database";
    static final String ERROR_TRACKS_NOT_IN_FILES = "Tracks not in files";
    static final String ERROR_TRACKS_NOT_FOUND_FOR_ARTIFACT = "Tracks not found for artifact: %s";
    static final String ERROR_TRACK_FILES_NOT_FOUND_FOR_ARTIFACT = "Track files not found for artifact: %s";
    static final String INFO_TRACKS_VALIDATED = "Tracks validated";
    static final String INFO_TRACKS_LOADED = "Tracks loaded";

    static final String ERROR_MEDIA_FILES_NOT_IN_DB = "Media files not in database";
    static final String ERROR_MEDIA_FILES_NOT_IN_FILES = "Media files not in files";
    static final String ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_DB = "Artifact media files not in database";
    static final String ERROR_ARTIFACT_MEDIA_FILES_NOT_IN_FILES = "Artifact media files not in files";
    static final String ERROR_MEDIA_FILES_EMPTY_SIZE = "Media files with empty size";
    static final String INFO_MEDIA_FILES_VALIDATED = "Media files validated";
    static final String INFO_ARTIFACT_MEDIA_FILES_VALIDATED = "Artifact media files validated";
    static final String INFO_MEDIA_FILES_LOADED = "Media files loaded";
    static final String INFO_MEDIA_FILES_SIZE_VALIDATED = "Media files size validated";

    static final String INFO_ARTISTS_LOADED = "Artists loaded";
    static final String INFO_ARTISTS_CLEANSED = "Artists cleansed";

    static final String INFO_ARTIFACTS_LOADED = "Artifacts loaded";

    static final String INFO_ARTIFACTS_IMPORTED = "Artifacts imported";
    static final String INFO_TRACKS_IMPORTED = "Tracks imported";
    static final String INFO_PRODUCTS_TRACKS_IMPORTED = "Products for tracks imported";
    static final String INFO_MEDIA_FILES_IMPORTED = "Media files imported";

    static final String INFO_CATEGORIES_IMPORTED = "Categories imported";
    static final String INFO_PRODUCTS_IMPORTED = "Products imported";

    static final String PROCESSING_ARTIST = "Processing artist: %s";
    static final String PROCESSING_ARTIFACT = "Processing artifact: %s";
    static final String PROCESSING_SAVING_ARTIFACT = "Saving artifact: %s";
    static final String PROCESSING_PREPARING_ARTIFACT = "Preparing artifact: %s";
    static final String PROCESSING_TRACKS = "Processing tracks: %s";
    static final String PROCESSING_TRACK = "Processing track: %s";
    static final String PROCESSING_PARSING_CUE = "Parsing cue: %s";
    static final String PROCESSING_CUE = "Processing cue: %s";
    static final String PROCESSING_PARSING_MEDIA_FILE = "Parsing media file: %s";
    static final String PROCESSING_PARSED_MEDIA_FILE = "Parsed media file: %s";
    static final String PROCESSING_SAVING_TRACKS_WITH_MEDIA = "Saving tracks with media: %s";
    static final String PROCESSING_SAVING_TRACK_WITH_MEDIA = "Saving track with media: %s";

    static final String VALIDATING_LOADING_FROM_PATH = "Loading validation information from path: %s";
    static final String VALIDATING_LOADING_FROM_DB = "Loading validation information from database";
    static final String VALIDATING_ARTISTS = "Validating artists";
    static final String VALIDATING_ARTIFACTS = "Validating artifacts";
    static final String VALIDATING_TRACKS = "Validating tracks";
    static final String VALIDATING_MEDIA_FILES = "Validating media files";
    static final String VALIDATING_ARTIFACT_MEDIA_FILES = "Validating artifact media files";
}
