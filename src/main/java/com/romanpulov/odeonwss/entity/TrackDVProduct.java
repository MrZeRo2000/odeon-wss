package com.romanpulov.odeonwss.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "tracks_dv_products")
public class TrackDVProduct {
    @Id
    @Column(name = "dvpd_id")
    private Long dvProductId;


    @Column(name = "trck_id")
    private Long trackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackDVProduct that = (TrackDVProduct) o;
        return trackId.equals(that.trackId) && dvProductId.equals(that.dvProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, dvProductId);
    }

    @Override
    public String toString() {
        return "TrackMediaFile{" +
                "trackId=" + trackId +
                ", dvProductId=" + dvProductId +
                '}';
    }
}
