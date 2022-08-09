package com.romanpulov.odeonwss.service.processor;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.romanpulov.odeonwss.service.processor.MDBConst.MDB_FILE_NAME;
import static com.romanpulov.odeonwss.service.processor.ProcessorMessages.*;

public abstract class AbstractMDBImportProcessor extends AbstractFileSystemProcessor{
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
