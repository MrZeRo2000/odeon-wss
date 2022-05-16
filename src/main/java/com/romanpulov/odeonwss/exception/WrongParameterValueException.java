package com.romanpulov.odeonwss.exception;

public class WrongParameterValueException extends Exception {
    public WrongParameterValueException(String parameterName, String message) {
        super(String.format("Wrong parameter %s value: %s", parameterName, message));
    }
}
