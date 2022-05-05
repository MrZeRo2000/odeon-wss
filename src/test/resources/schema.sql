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

DROP TABLE IF EXISTS media_files;

CREATE TABLE media_files (
    mdfl_id INTEGER PRIMARY KEY AUTOINCREMENT,
    mdfl_name TEXT NOT NULL,
    mdfl_format_code TEXT NOT NULL,
    mdfl_size INTEGER NOT NULL,
    mdfl_bitrate INTEGER NULL,
    mdfl_duration INTEGER NULL
);

DROP TABLE IF EXISTS artifacts;

CREATE TABLE artifacts (
    artf_id INTEGER PRIMARY KEY AUTOINCREMENT,
    attp_id INTEGER NOT NULL,
    arts_id INTEGER NULL,
    mdfl_id INTEGER NULL,
    artf_title TEXT NOT NULL,
    artf_year INTEGER NULL,
    artf_duration INTEGER NULL,
    artf_ins_date INTEGER NULL
);

CREATE INDEX idx_artifact_attp ON artifacts (attp_id);

CREATE INDEX idx_artifact_arts ON artifacts (arts_id);

CREATE UNIQUE INDEX idx_artifact_attp_arts_title_year ON artifacts(attp_id, arts_id, artf_title, artf_year);

DROP TABLE IF EXISTS compositions;

CREATE TABLE compositions (
    comp_id INTEGER PRIMARY KEY AUTOINCREMENT,
    artf_id INTEGER NOT NULL,
    mdfl_id INTEGER NOT NULL,
    comp_title TEXT NOT NULL,
    comp_duration INTEGER NULL,
    comp_disk_num INTEGER NULL,
    comp_num INTEGER NULL
);

CREATE INDEX idx_composition_artf ON compositions(artf_id);
