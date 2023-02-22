package com.romanpulov.odeonwss.service.processor;

import com.romanpulov.odeonwss.service.processor.model.ProcessingActionType;
import com.romanpulov.odeonwss.service.processor.model.ProcessDetail;

import java.util.List;

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

    protected void infoHandler(String infoMessage) {
        progressHandler.handleProgress(ProcessDetail.fromInfoMessage(infoMessage));
    }

    protected void infoHandler(String infoMessage, int rows) {
        progressHandler.handleProgress(ProcessDetail.fromInfoMessage(infoMessage, rows));
    }

    protected void errorHandler(String errorMessage) {
        progressHandler.handleProgress(ProcessDetail.fromErrorMessage(errorMessage));
    }

    protected void errorHandler(String errorMessage, List<String> items) {
        progressHandler.handleProgress(ProcessDetail.fromErrorMessage(errorMessage, items));
    }

    protected void errorHandler(String errorMessage, Object ...args) {
        progressHandler.handleProgress(ProcessDetail.fromErrorMessage(errorMessage, args));
    }

    protected void errorHandlerItem(String errorMessage, String item) {
        progressHandler.handleErrorItem(errorMessage, item);
    }

    protected void warningHandler(String warningMessage) {
        progressHandler.handleProgress(ProcessDetail.fromWarningMessage(warningMessage));
    }

    protected void warningHandlerWithAddArtistAction(String warningMessage, String artistName) {
        progressHandler.handleProgress(ProcessDetail.fromWarningMessageWithAction(warningMessage, ProcessingActionType.ADD_ARTIST, artistName));
    }
}
