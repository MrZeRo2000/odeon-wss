package com.romanpulov.odeonwss.service.processor;

public abstract class AbstractProcessor {
    private ProgressHandler progressHandler;

    public ProgressHandler getProgressHandler() {
        return progressHandler;
    }

    public void setProgressHandler(ProgressHandler progressHandler) {
        this.progressHandler = progressHandler;
    }

    protected String rootFolder;

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    abstract public void execute() throws ProcessorException;

    protected void infoHandler(String errorMessage) {
        progressHandler.handleProgress(ProgressDetail.fromInfoMessage(errorMessage));
    }

    protected void errorHandler(String errorMessage) {
        progressHandler.handleProgress(ProgressDetail.fromErrorMessage(errorMessage));
    }

    protected void warningHandler(String warningMessage) {
        progressHandler.handleProgress(ProgressDetail.fromWarningMessage(warningMessage));
    }

    protected void warningHandlerWithAddArtistAction(String warningMessage, String artistName) {
        progressHandler.handleProgress(ProgressDetail.fromWarningMessageWithAction(warningMessage, ProcessingActionType.ADD_ARTIST, artistName));
    }
}
