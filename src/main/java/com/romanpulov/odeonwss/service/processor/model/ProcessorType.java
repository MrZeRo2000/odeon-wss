package com.romanpulov.odeonwss.service.processor.model;

public enum ProcessorType {
    ARTISTS_IMPORTER("Artists importer"),
    CLASSICS_IMPORTER("Classics importer"),
    CLASSICS_VALIDATOR("Classics validator"),
    DV_MUSIC_IMPORTER("Video music importer"),
    DV_MUSIC_MEDIA_LOADER("Video music media loader"),
    DV_MUSIC_VALIDATOR("Video music validator"),
    DV_PRODUCT_IMPORTER("Video product importer"),
    DV_MOVIES_IMPORTER("Movies importer"),
    MP3_LOADER("MP3 Loader"),
    MP3_VALIDATOR("MP3 Validator"),
    LA_LOADER("LA Loader"),
    LA_VALIDATOR("LA Validator");

    public final String label;

    ProcessorType(String label) {
        this.label = label;
    }

    public static ProcessorType valueOfLabel(String label) {
        for (ProcessorType e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
