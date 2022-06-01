package com.romanpulov.odeonwss.entity;

import java.util.stream.Stream;

public enum ArtistCategoryType {
    GENRE("G"), STYLE("S");

    private final String code;

    ArtistCategoryType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ArtistCategoryType fromCode(String code) {
        return Stream.of(ArtistCategoryType.values())
                .filter(artistCategoryType -> artistCategoryType.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
