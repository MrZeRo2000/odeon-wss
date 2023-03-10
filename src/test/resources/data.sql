INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (100, 'Music', null);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (101, 'MP3', 'MP3', 100);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (102, 'LA', 'APE|FLAC|WV', 100);

INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (200, 'Video', null);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (201, 'Music', 'AVI|M4V|MKV|MP4|MPG', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (202, 'Movies', 'AVI|M4V|MKV|MP4|MPG', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (203, 'Animation', 'AVI|M4V|MKV|MP4|MPG', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (204, 'Documentary', 'AVI|M4V|MKV|MP4|MPG', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_media_file_formats, attp_parent_id)
VALUES (205, 'Other', 'AVI|M4V|MKV|MP4|MPG', 200);

INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(1, 'VHS');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(2, 'MPEG4 DVD');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(3, 'DVD');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(4, 'MPEG4 VHS');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(5, 'MPEG4 TV');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(6, 'HDTV Rip');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(7, 'AVC');
INSERT INTO dv_types(dvtp_id, dvtp_name)
VALUES(8, 'DVD Rip');
