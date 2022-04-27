package com.romanpulov.odeonwss.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "artists", indexes = @Index(
        name = "idx_artists_arts_name",
        columnList = "arts_type_code, arts_name",
        unique = true)
)
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "arts_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "arts_type_code")
    @NotNull
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "arts_name")
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist() {
    }

    public Artist(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
