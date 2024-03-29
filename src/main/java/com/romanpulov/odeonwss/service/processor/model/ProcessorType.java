package com.romanpulov.odeonwss.service.processor.model;

public enum ProcessorType {
    ARTISTS_IMPORTER("Artists importer"),
    DV_PRODUCT_IMPORTER("Video product importer"),
    MP3_UPDATE_ATTRIBUTES_IMPORTER("MP3 update attributes importer"),
    LA_UPDATE_ATTRIBUTES_IMPORTER("LA update attributes importer"),

    CLASSICS_IMPORTER("Classics importer"),
    CLASSICS_VALIDATOR("Classics validator"),

    MP3_LOADER("MP3 Loader"),
    MP3_VALIDATOR("MP3 Validator"),
    LA_LOADER("LA Loader"),
    LA_VALIDATOR("LA Validator"),

    DV_MUSIC_IMPORTER("Video music importer"),
    DV_MUSIC_LOADER("Video music Loader"),
    DV_MUSIC_VALIDATOR("Video music validator"),
    DV_MUSIC_MEDIA_LOADER("Video music media loader"),

    DV_MOVIES_IMPORTER("Movies importer"),
    DV_MOVIES_LOADER("Movies Loader"),
    DV_MOVIES_VALIDATOR("Movies Validator"),
    DV_MOVIES_MEDIA_LOADER("Movies media loader"),

    DV_ANIMATION_LOADER("Animation Loader"),
    DV_ANIMATION_VALIDATOR("Animation Validator"),
    DV_ANIMATION_MEDIA_LOADER("Animation media loader"),
    ;

    public final String label;

    ProcessorType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
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
