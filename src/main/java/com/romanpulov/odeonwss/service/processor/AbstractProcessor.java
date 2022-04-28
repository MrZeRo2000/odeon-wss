package com.romanpulov.odeonwss.service.processor;

public abstract class AbstractProcessor {
    protected final ProgressHandler progressHandler;

    public AbstractProcessor(ProgressHandler progressHandler) {
        this.progressHandler = progressHandler;
    }

    abstract public void execute();
}
