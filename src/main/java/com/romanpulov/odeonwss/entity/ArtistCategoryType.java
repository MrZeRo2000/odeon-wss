package com.romanpulov.odeonwss.entity;

public enum ArtistCategoryType {
    GENRE("G"), STYLE("S");

    private final String code;

    ArtistCategoryType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
