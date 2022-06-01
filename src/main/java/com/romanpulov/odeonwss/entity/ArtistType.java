package com.romanpulov.odeonwss.entity;

import java.util.stream.Stream;

public enum ArtistType {
    ARTIST("A"), CLASSICS("C");

    private final String code;

    ArtistType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ArtistType fromCode(String code) {
        return Stream.of(ArtistType.values())
                .filter(artistType -> artistType.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
