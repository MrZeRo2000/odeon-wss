DROP TABLE IF EXISTS artists;

CREATE TABLE artists (
    arts_id INTEGER PRIMARY KEY AUTOINCREMENT,
    arts_type_code TEXT NOT NULL,
    arts_name TEXT NOT NULL,
    arts_migration_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_artists_arts_type_code_name ON artists (arts_type_code, arts_name)
;

DROP TABLE IF EXISTS artifact_types;

CREATE TABLE artifact_types (
    attp_id INTEGER PRIMARY KEY AUTOINCREMENT,
    attp_name TEXT NOT NULL,
    attp_parent_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_artifact_types_name ON artifact_types (attp_name)
;