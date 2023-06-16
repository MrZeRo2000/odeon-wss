package com.romanpulov.odeonwss.exception;

public class EmptyParameterException extends Exception {
    public EmptyParameterException(String parameterName) {
        super(String.format("Empty parameter: %s", parameterName));
    }
}
