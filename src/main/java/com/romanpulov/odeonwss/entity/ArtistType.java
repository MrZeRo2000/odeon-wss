package com.romanpulov.odeonwss.entity;

public enum ArtistType {
    ARTIST("A"), CLASSICS("C");

    private final String code;

    ArtistType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
