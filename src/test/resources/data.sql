INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (100, 'Music', null);
INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (101, 'MP3', 100);
INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (102, 'LA', 100);

INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (200, 'Video', null);
INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (201, 'Music', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (202, 'Movies', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (203, 'Animation', 200);
INSERT INTO artifact_types (attp_id, attp_name, attp_parent_id)
VALUES (204, 'Documentary', 200);

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
