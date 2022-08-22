package com.romanpulov.odeonwss.service.processor;

import java.util.HashSet;
import java.util.Set;

abstract public class AbstractValidateProcessor extends AbstractFileSystemProcessor {

    protected boolean compareStringSets(
            Set<String> pathStrings,
            Set<String> dbStrings,
            String pathErrorMessage,
            String dbErrorMessage)
    {
        boolean result = true;

        Set<String> pathStringsDiff = new HashSet<>(pathStrings);
        pathStringsDiff.removeAll(dbStrings);
        if (!pathStringsDiff.isEmpty()) {
            errorHandler(dbErrorMessage, String.join(",", pathStringsDiff));
            result = false;
        }

        Set<String> dbStringsDiff = new HashSet<>(dbStrings);
        dbStringsDiff.removeAll(pathStrings);
        if (!dbStringsDiff.isEmpty()) {
            errorHandler(pathErrorMessage, String.join(",", dbStringsDiff));
            result = false;
        }

        return result;

    }
}
