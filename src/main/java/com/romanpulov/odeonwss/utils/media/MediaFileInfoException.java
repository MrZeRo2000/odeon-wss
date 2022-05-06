package com.romanpulov.odeonwss.utils.media;

public class MediaFileInfoException extends Exception {
    public MediaFileInfoException(String fileName, String errorMessage) {
        super(String.format("Error getting media information from %s: %s", fileName, errorMessage));
    }
}
