DROP TABLE IF EXISTS media_files_2024_02_16;

CREATE TABLE media_files_2024_02_16 AS
SELECT * FROM media_files;

DROP TABLE IF EXISTS media_files;

CREATE TABLE media_files (
    mdfl_id INTEGER PRIMARY KEY AUTOINCREMENT,
    artf_id INTEGER NULL,
    mdfl_name TEXT NOT NULL,
    mdfl_format_code TEXT NOT NULL,
    mdfl_size INTEGER NOT NULL,
    mdfl_bitrate INTEGER NULL,
    mdfl_duration INTEGER NULL,
    mdfl_width INTEGER NULL,
    mdfl_height INTEGER NULL,
    mdfl_extra TEXT NULL,
    mdfl_ins_datm INTEGER NOT NULL,
    mdfl_upd_datm INTEGER NOT NULL,
    mdfl_migration_id INTEGER NULL
);

CREATE UNIQUE INDEX idx_media_files_artf_name ON media_files(artf_id, mdfl_name);

INSERT INTO media_files(
    mdfl_id,
    artf_id,
    mdfl_name,
    mdfl_format_code,
    mdfl_size,
    mdfl_bitrate,
    mdfl_duration,
    mdfl_ins_datm,
    mdfl_upd_datm,
    mdfl_migration_id
)
SELECT
    mdfl_id,
    artf_id,
    mdfl_name,
    mdfl_format_code,
    mdfl_size,
    mdfl_bitrate,
    mdfl_duration,
    mdfl_ins_datm,
    mdfl_upd_datm,
    mdfl_migration_id
FROM media_files_2024_02_16;

DROP TABLE IF EXISTS media_files_2024_02_16;