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

CREATE UNIQUE INDEX idx_artifact_attp_parent_id_attp_name ON artifact_types (attp_parent_id, attp_name)
;

DROP TABLE IF EXISTS dv_types;

CREATE TABLE dv_types (
    dvtp_id INTEGER PRIMARY KEY AUTOINCREMENT,
    dvtp_name TEXT NOT NULL
);

CREATE UNIQUE INDEX idx_dv_types_dvtp_name ON dv_types(dvtp_name);

DROP TABLE IF EXISTS artifacts;

CREATE TABLE artifacts (
    artf_id INTEGER PRIMARY KEY AUTOINCREMENT,
    attp_id INTEGER NOT NULL,
    arts_id INTEGER NULL,
    perf_arts_id INTEGER NULL,
    artf_title TEXT NOT NULL,
    artf_year INTEGER NULL,
    artf_duration INTEGER NULL,
    artf_size INTEGER NULL,
    artf_ins_date INTEGER NULL,
    artf_migration_id INTEGER NULL
);

CREATE INDEX idx_artifact_attp ON artifacts (attp_id);

CREATE INDEX idx_artifact_arts ON artifacts (arts_id);

CREATE UNIQUE INDEX idx_artifact_attp_arts_title_year ON artifacts(attp_id, arts_id, artf_title, artf_year);

DROP TABLE IF EXISTS tracks;

CREATE TABLE tracks (
    trck_id INTEGER PRIMARY KEY AUTOINCREMENT,
    artf_id INTEGER NOT NULL,
    arts_id INTEGER NULL,
    perf_arts_id INTEGER NULL,
    dvtp_id INTEGER NULL,
    trck_title TEXT NOT NULL,
    trck_duration INTEGER NULL,
    trck_disk_num INTEGER NULL,
    trck_num INTEGER NULL,
    trck_migration_id INTEGER NULL
);

CREATE INDEX idx_track_artf ON tracks(artf_id);

CREATE UNIQUE INDEX idx_track_artf_id_disk_num_num ON tracks(artf_id, trck_disk_num, trck_num);

DROP TABLE IF EXISTS media_files;

CREATE TABLE media_files (
    mdfl_id INTEGER PRIMARY KEY AUTOINCREMENT,
    artf_id INTEGER NULL,
    mdfl_name TEXT NOT NULL,
    mdfl_format_code TEXT NOT NULL,
    mdfl_size INTEGER NOT NULL,
    mdfl_bitrate INTEGER NULL,
    mdfl_duration INTEGER NULL,
    mdfl_migration_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_media_files_artf_name ON media_files(artf_id, mdfl_name);

DROP TABLE IF EXISTS tracks_media_files;

CREATE TABLE tracks_media_files(
    trck_id INTEGER NOT NULL,
    mdfl_id INTEGER NOT NULL,
    PRIMARY KEY (trck_id, mdfl_id)
);

DROP TABLE IF EXISTS artist_categories;

CREATE TABLE artist_categories (
    atct_id INTEGER PRIMARY KEY AUTOINCREMENT,
    arts_id INTEGER NOT NULL,
    atct_type_code TEXT NOT NULL,
    atct_name TEXT NOT NULL,
    atct_migration_id INTEGER NULL
);

CREATE INDEX idx_artist_categories_arts_id ON artist_categories(arts_id);

DROP TABLE IF EXISTS artist_details;

CREATE TABLE artist_details (
    atdt_id INTEGER PRIMARY KEY AUTOINCREMENT,
    arts_id INTEGER NOT NULL,
    atdt_biography TEXT NOT NULL
);

CREATE UNIQUE INDEX idx_artist_details_arts_id ON artist_details(arts_id);

DROP TABLE IF EXISTS artist_lyrics;

CREATE TABLE artist_lyrics (
    atlr_id INTEGER PRIMARY KEY AUTOINCREMENT,
    arts_id INTEGER NOT NULL,
    atlr_title TEXT NOT NULL,
    atlr_text TEXT NOT NULL
);

CREATE UNIQUE INDEX idx_artist_lyrics_arts_id_atlr_title ON artist_lyrics(arts_id, atlr_title);

DROP TABLE IF EXISTS dv_origins;

CREATE TABLE dv_origins (
    dvor_id INTEGER PRIMARY KEY AUTOINCREMENT,
    dvor_name TEXT NOT NULL,
    dvor_migration_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_dv_origins_dvor_name ON dv_origins(dvor_name);

DROP TABLE IF EXISTS dv_categories;

CREATE TABLE dv_categories (
    dvct_id INTEGER PRIMARY KEY AUTOINCREMENT,
    dvct_name TEXT NOT NULL,
    dvct_migration_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_dv_categories_dvct_name ON dv_categories(dvct_name);

DROP TABLE IF EXISTS dv_products;

CREATE TABLE dv_products(
    dvpd_id INTEGER PRIMARY KEY AUTOINCREMENT,
    attp_id INTEGER NOT NULL,
    dvor_id INTEGER NOT NULL,
    dvpd_title TEXT NOT NULL,
    dvpd_orig_title TEXT,
    dvpd_year INTEGER,
    dvpd_front_info TEXT,
    dvpd_description TEXT,
    dvpd_notes TEXT,
    dvpd_migration_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_dv_products_dvpd_name ON dv_products(dvpd_title);

CREATE INDEX idx_dv_products_attp_id ON dv_products(attp_id);
CREATE INDEX idx_dv_products_dvor_id ON dv_products(dvor_id);

DROP TABLE IF EXISTS dv_products_dv_categories;

CREATE TABLE dv_products_dv_categories(
    dvpd_id INTEGER NOT NULL,
    dvct_id INTEGER NOT NULL,
    PRIMARY KEY (dvpd_id, dvct_id)
);

CREATE INDEX idx_dv_products_dv_categories_dvpd_id ON dv_products_dv_categories(dvpd_id);

DROP TABLE IF EXISTS tracks_dv_products;

CREATE TABLE tracks_dv_products(
    trck_id INTEGER NOT NULL,
    dvpd_id INTEGER NOT NULL,
    PRIMARY KEY (trck_id, dvpd_id)
);

CREATE UNIQUE INDEX idx_tracks_dv_products_trck_id ON tracks_dv_products(trck_id);

CREATE UNIQUE INDEX idx_tracks_dv_products_dvpd_id ON tracks_dv_products(dvpd_id);

-- PROCESSING --

DROP TABLE IF EXISTS process_info;

CREATE TABLE process_info(
    prif_id INTEGER PRIMARY KEY AUTOINCREMENT,
    prif_type TEXT NOT NULL,
    prif_status TEXT NOT NULL,
    prif_upd_datm INTEGER NOT NULL
);

CREATE INDEX idx_process_info_upd_datm ON process_info(prif_upd_datm);

DROP TABLE IF EXISTS process_details;

CREATE TABLE process_details(
    prdt_id INTEGER PRIMARY KEY AUTOINCREMENT,
    prif_id INTEGER NOT NULL,
    prdt_message TEXT NOT NULL,
    prdt_status TEXT NOT NULL,
    prdt_rows INTEGER NULL,
    prdt_upd_datm INTEGER NOT NULL
);

DROP TABLE IF EXISTS process_detail_items;

CREATE TABLE process_detail_items(
    prdi_id INTEGER PRIMARY KEY AUTOINCREMENT,
    prdt_id INTEGER NOT NULL,
    prdi_value TEXT NOT NULL
);

DROP TABLE IF EXISTS process_detail_actions;

CREATE TABLE process_detail_actions(
    prda_id INTEGER PRIMARY KEY AUTOINCREMENT,
    prdt_id INTEGER NOT NULL,
    prda_type TEXT NOT NULL,
    prda_value TEXT NOT NULL
);