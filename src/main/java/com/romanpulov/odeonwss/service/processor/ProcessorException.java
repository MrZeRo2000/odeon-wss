package com.romanpulov.odeonwss.service.processor;

public class ProcessorException extends Exception{
    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Object ...args) {
        super(String.format(message, args));
    }
}
