package com.romanpulov.odeonwss.exception;

public class CommonEntityAlreadyExistsException extends Exception {
    public CommonEntityAlreadyExistsException(String entityName, Long id) {
        super(String.format("Entity %s already exists with id=%d", entityName, id));
    }
}
