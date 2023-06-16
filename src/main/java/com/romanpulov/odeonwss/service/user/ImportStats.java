package com.romanpulov.odeonwss.service.user;

public class ImportStats {
    private int rowsInserted;
    private int rowsUpdated;

    public int getRowsInserted() {
        return rowsInserted;
    }

    public void setRowsInserted(int rowsInserted) {
        this.rowsInserted = rowsInserted;
    }

    public int getRowsUpdated() {
        return rowsUpdated;
    }

    public void setRowsUpdated(int rowsUpdated) {
        this.rowsUpdated = rowsUpdated;
    }

    public void incrementRowsInserted() {
        addRowsInserted(1);
    }

    public void addRowsInserted(int rows) {
        this.rowsInserted += rows;
    }

    public void incrementRowsUpdated() {
        addRowsUpdated(1);
    }

    public void addRowsUpdated(int rows) {
        this.rowsUpdated += rows;
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
