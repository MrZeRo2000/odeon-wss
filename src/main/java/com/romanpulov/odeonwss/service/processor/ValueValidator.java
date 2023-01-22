package com.romanpulov.odeonwss.service.processor;

public class ValueValidator {
    public static boolean isEmpty(Long value) {
        return (value == null) || (value == 0L);
    }
}
