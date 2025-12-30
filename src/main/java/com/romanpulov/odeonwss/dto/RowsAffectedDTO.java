package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowsAffectedDTO {
    private long rowsAffected;

    public long getRowsAffected() {
        return rowsAffected;
    }

    public void setRowsAffected(long rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    private RowsAffectedDTO() {
    }

    public static RowsAffectedDTO from(long rowsAffected) {
        RowsAffectedDTO instance = new RowsAffectedDTO();
        instance.setRowsAffected(rowsAffected);
        return instance;
    }
}
