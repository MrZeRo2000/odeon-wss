package com.romanpulov.odeonwss.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "arts_migration_id")
    @Nullable
    private Long migrationId;

    @Nullable
    public Long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(@Nullable Long migrationId) {
        this.migrationId = migrationId;
    }

    public Artist() {
    }

    public Artist(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
