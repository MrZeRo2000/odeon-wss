INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('table', 'sqlite_sequence', 'sqlite_sequence', 3, 'CREATE TABLE sqlite_sequence(name,seq)');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('table', 'artists', 'artists', 2, 'CREATE TABLE artists ( arts_id INTEGER PRIMARY KEY AUTOINCREMENT, arts_type_code TEXT NOT NULL, arts_name TEXT NOT NULL, arts_migration_id INTEGER NULL )');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_artists_arts_type_code_name', 'artists', 4, 'CREATE UNIQUE INDEX idx_artists_arts_type_code_name ON artists (arts_type_code, arts_name) ');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('table', 'artifact_types', 'artifact_types', 5, 'CREATE TABLE artifact_types ( attp_id INTEGER PRIMARY KEY AUTOINCREMENT, attp_name TEXT NOT NULL, attp_parent_id INTEGER NULL )');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_artifact_types_name', 'artifact_types', 6, 'CREATE UNIQUE INDEX idx_artifact_types_name ON artifact_types (attp_name) ');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('table', 'artifacts', 'artifacts', 8, 'CREATE TABLE artifacts ( artf_id INTEGER PRIMARY KEY AUTOINCREMENT, attp_id INTEGER NOT NULL, arts_id INTEGER NULL, artf_title TEXT NOT NULL, artf_year INTEGER NULL, artf_duration INTEGER NULL, artf_size INTEGER NULL, artf_ins_date INTEGER NULL )');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_artifact_attp', 'artifacts', 9, 'CREATE INDEX idx_artifact_attp ON artifacts (attp_id)');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_artifact_arts', 'artifacts', 10, 'CREATE INDEX idx_artifact_arts ON artifacts (arts_id)');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_artifact_attp_arts_title_year', 'artifacts', 11, 'CREATE UNIQUE INDEX idx_artifact_attp_arts_title_year ON artifacts(attp_id, arts_id, artf_title, artf_year)');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('table', 'compositions', 'compositions', 12, 'CREATE TABLE compositions ( comp_id INTEGER PRIMARY KEY AUTOINCREMENT, artf_id INTEGER NOT NULL, comp_title TEXT NOT NULL, comp_duration INTEGER NULL, comp_disk_num INTEGER NULL, comp_num INTEGER NULL )');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_composition_artf', 'compositions', 13, 'CREATE INDEX idx_composition_artf ON compositions(artf_id)');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('table', 'media_files', 'media_files', 7, 'CREATE TABLE media_files ( mdfl_id INTEGER PRIMARY KEY AUTOINCREMENT, artf_id INTEGER NULL, comp_id INTEGER NULL, mdfl_name TEXT NOT NULL, mdfl_format_code TEXT NOT NULL, mdfl_size INTEGER NOT NULL, mdfl_bitrate INTEGER NULL, mdfl_duration INTEGER NULL )');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_media_files_artf', 'media_files', 14, 'CREATE INDEX idx_media_files_artf ON media_files(artf_id)');
INSERT INTO sqlite_master (type, name, tbl_name, rootpage, sql) VALUES ('index', 'idx_media_files_comp', 'media_files', 15, 'CREATE INDEX idx_media_files_comp ON media_files(comp_id)');