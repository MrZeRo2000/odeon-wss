package com.romanpulov.odeonwss.dto;

public class TransformRules {
    static boolean booleanFromLong(Long value) {
        return value != null && value.equals(1L);
    }
}
