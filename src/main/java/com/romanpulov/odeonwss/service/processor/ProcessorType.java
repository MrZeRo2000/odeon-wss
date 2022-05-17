package com.romanpulov.odeonwss.service.processor;

public enum ProcessorType {
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
