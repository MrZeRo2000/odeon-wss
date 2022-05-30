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

    protected static final String ARTISTLIST_ID_COLUMN_NAME = "ArtistListID";
    protected static final String TITLE_COLUMN_NAME = "Title";
    protected static final String NOTES_COLUMN_NAME = "Notes";
    protected static final String SOURCE_COLUMN_NAME = "Source";
    protected static final String ARTISTLISTCAT_ID_COLUMN_NAME = "ArtistListCatID";
    protected static final String CAT_ID_COLUMN_NAME = "CatID";

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
    protected Path validateAndGetPath() throws ProcessorException {
        Path path = super.validateAndGetPath().resolve(MDB_FILE_NAME);

        if (Files.notExists(path)) {
            throw new ProcessorException(ProcessorMessages.ERROR_FILE_NOT_FOUND, path.toAbsolutePath().toString());
        }

        return path;
    }
}
