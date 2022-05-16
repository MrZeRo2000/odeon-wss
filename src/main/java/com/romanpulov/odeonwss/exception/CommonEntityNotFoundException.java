package com.romanpulov.odeonwss.exception;

public class CommonEntityNotFoundException extends Exception {
    public CommonEntityNotFoundException(Long id) {
        super(String.format("Entity with id=%d not found", id));
    }
}
