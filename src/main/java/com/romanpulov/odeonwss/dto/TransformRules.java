package com.romanpulov.odeonwss.dto;

public class TransformRules {
    static Boolean booleanFromLong(Long value) {
        return value != null && value.equals(1L) ? true : null;
    }
}
