package com.romanpulov.odeonwss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowsAffectedDTO {
    private long RowsAffected;

    public long getRowsAffected() {
        return RowsAffected;
    }

    public void setRowsAffected(long rowsAffected) {
        RowsAffected = rowsAffected;
    }

    private RowsAffectedDTO() {
    }

    public static RowsAffectedDTO from(long rowsAffected) {
        RowsAffectedDTO instance = new RowsAffectedDTO();
        instance.setRowsAffected(rowsAffected);
        return instance;
    }
}
