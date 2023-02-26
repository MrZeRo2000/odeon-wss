package com.romanpulov.odeonwss.service.processor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProcessDetailInfo {
    private final String message;

    public String getMessage() {
        return message;
    }

    private final List<String> items;

    public List<String> getItems() {
        return items;
    }

    private ProcessDetailInfo(String message, List<String> items) {
        this.message = message;
        this.items = items;
    }

    public static ProcessDetailInfo fromMessageItems(String message, List<String> items) {
        return new ProcessDetailInfo(message, items);
    }

    public static ProcessDetailInfo fromMessage(String message) {
        return new ProcessDetailInfo(message, new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessDetailInfo that = (ProcessDetailInfo) o;
        return message.equals(that.message) && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, items);
    }

    @Override
    public String toString() {
        return "ProgressInfo{" +
                "message='" + message + '\'' +
                ", items=" + items +
                '}';
    }
}
