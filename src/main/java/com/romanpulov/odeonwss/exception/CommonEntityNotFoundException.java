package com.romanpulov.odeonwss.exception;

public class CommonEntityNotFoundException extends DataNotFoundException {
    public CommonEntityNotFoundException(Long id) {
        super(String.format("Entity with id=%d not found", id));
    }

    public CommonEntityNotFoundException(String entityName, Long id) {
        super(String.format("Entity %s with id=%d not found", entityName, id));
    }

}
