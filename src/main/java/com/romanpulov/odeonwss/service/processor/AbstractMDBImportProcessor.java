package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.*;

public abstract class AbstractMDBImportProcessor extends AbstractFileSystemProcessor{
    private static final String MDB_FILE_NAME = "Cat2000.mdb";

    protected static final String ARTISTLIST_TABLE_NAME = "ArtistList";
    protected static final String ARTISTLISTCAT_TABLE_NAME = "ArtistListCat";
    protected static final String ARTISTLYRICS_TABLE_NAME = "ArtistLyrics";
    protected static final String CLASSICS_TABLE_NAME = "CLCont";

    protected static final String ARTISTLIST_ID_COLUMN_NAME = "ArtistListID";
    protected static final String TITLE_COLUMN_NAME = "Title";
    protected static final String NOTES_COLUMN_NAME = "Notes";
    protected static final String SOURCE_COLUMN_NAME = "Source";
    protected static final String ARTISTLISTCAT_ID_COLUMN_NAME = "ArtistListCatID";
    protected static final String CAT_ID_COLUMN_NAME = "CatID";
    protected static final String ARTISTLYRICS_ID_COLUMN_NAME = "ArtistLyricsID";
    protected static final String SONGNAME_COLUMN_NAME = "SongName";
    protected static final String LYRICSTEXT_COLUMN_NAME = "LyricsText";
    protected static final String CLASSICS_ID_COLUMN_NAME = "CLContID";
    protected static final String PERFORMER_ARTISTLIST_ID_COLUMN_NAME = "PerArtistListID";
    protected static final String YEAR_COLUMN_NAME = "Year";
    protected static final String REC_ID_COLUMN_NAME = "RecID";
    protected static final String DIR_NAME_COLUMN_NAME = "DirName";


    protected static class MDBReader implements Closeable {
        private final Database database;

        public MDBReader(File mFile) throws ProcessorException {
            try {
                this.database = DatabaseBuilder.open(mFile);
            } catch (IOException e) {
                throw new ProcessorException(ERROR_OPENING_MDB_DATABASE, e.getMessage());
            }
        }

        public Table getTable(String tableName) throws ProcessorException {
            try {
                return this.database.getTable(tableName);
            } catch (IOException e) {
                throw new ProcessorException(ERROR_OPENING_MDB_TABLE, tableName, e.getMessage());
            }
        }

        @Override
        public void close() throws IOException {
            this.database.close();
        }
    }

    @Override
    public void execute() throws ProcessorException {
        Path path = validateAndGetPath();

        try (MDBReader mdbReader = new MDBReader(path.toFile())) {
            importMDB(mdbReader);

        } catch (IOException e) {
            throw new ProcessorException(ERROR_PROCESSING_MDB_DATABASE, e.getMessage());
        }
    }

    protected abstract void importMDB(MDBReader mdbReader) throws ProcessorException;

    @Override
    protected Path validateAndGetPath() throws ProcessorException {
        Path path = super.validateAndGetPath().resolve(MDB_FILE_NAME);

        if (Files.notExists(path)) {
            throw new ProcessorException(ProcessorMessages.ERROR_FILE_NOT_FOUND, path.toAbsolutePath().toString());
        }

        return path;
    }
}
