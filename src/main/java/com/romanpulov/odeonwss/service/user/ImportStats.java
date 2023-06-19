package com.romanpulov.odeonwss.service.user;

import java.util.ArrayList;
import java.util.List;

public class ImportStats {
    private final List<String> rowsInserted = new ArrayList<>();
    private final List<String> rowsUpdated = new ArrayList<>();

    public List<String> getRowsInserted() {
        return rowsInserted;
    }

    public List<String> getRowsUpdated() {
        return rowsUpdated;
    }
    public void addRowInserted(String row) {
        this.rowsInserted.add(row);
    }

    public void addRowUpdated(String row) {
        this.rowsUpdated.add(row);
    }

    @Override
    public String toString() {
        return "ImportStats{" +
                "rowsInserted=" + rowsInserted +
                ", rowsUpdated=" + rowsUpdated +
                '}';
    }

    private ImportStats() {}

    public static ImportStats empty() {
        return new ImportStats();
    }
}
