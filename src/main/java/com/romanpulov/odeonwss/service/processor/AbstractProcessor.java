package com.romanpulov.odeonwss.service.processor;

public abstract class AbstractProcessor {
    private ProgressHandler progressHandler;

    public ProgressHandler getProgressHandler() {
        return progressHandler;
    }

    public void setProgressHandler(ProgressHandler progressHandler) {
        this.progressHandler = progressHandler;
    }

    protected String rootPath;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    abstract public void execute() throws Exception;
}
