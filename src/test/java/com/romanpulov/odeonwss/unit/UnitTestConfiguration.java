package com.romanpulov.odeonwss.unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UnitTestConfiguration {
    public static String getTestFilesPath() {
        try (
                InputStream is = UnitTestConfiguration
                        .class
                        .getClassLoader()
                        .getResourceAsStream("application.properties")
                ) {
            if (is != null) {
                try (
                        InputStreamReader reader = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(reader)) {
                    String testDataPathLine  = br
                            .lines()
                            .filter(line -> line.startsWith("test.data.path"))
                            .findFirst()
                            .orElseThrow();
                    return testDataPathLine.split("=")[1];
                }
            } else {
                throw new RuntimeException("Error loading application.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
