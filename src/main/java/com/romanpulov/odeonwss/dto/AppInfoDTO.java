package com.romanpulov.odeonwss.dto;

import java.util.Objects;

public class AppInfoDTO {
    private final String version;

    public String getVersion() {
        return version;
    }

    public AppInfoDTO(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppInfoDTO that = (AppInfoDTO) o;
        return version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {
        return "AppInfoDTO{" +
                "version='" + version + '\'' +
                '}';
    }
}
