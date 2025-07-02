package com.romanpulov.odeonwss.unit;

public class MediaTestData {
    static final String TEST_MP3_FILE_NAME =
            UnitTestConfiguration.getFullTestFilesPath("sample_mp3_3.mp3");
    static final long TEST_MP3_FILE_DURATION = 19L;
    static final long TEST_MP3_FILE_SIZE = 305086;
    static final long TEST_MP3_FILE_BITRATE = 128;

    static final String TEST_LA_FILE_NAME=
            UnitTestConfiguration.getFullTestFilesPath("sample_flac_2.flac");
    static final long TEST_LA_FILE_DURATION = 20L;
    static final long TEST_LA_FILE_SIZE = 1565617L;
    static final long TEST_LA_FILE_BITRATE = 623L;
    static final long TEST_LA_FILE_BITRATE_FFMPEG = 626L;

    static final String TEST_MP3_MS_FILE_NAME =
            UnitTestConfiguration.getFullTestFilesPath("sample_mp3_4.mp3");
    static final long TEST_MP3_MS_FILE_DURATION = 178L;
    static final long TEST_MP3_MS_FILE_SIZE = 7236266;
    static final long TEST_MP3_MS_FILE_BITRATE = 320;

    static final String TEST_MP3_EXTRA_FILE_NAME =
            UnitTestConfiguration.getFullTestFilesPath("sample_mp3_5.mp3");
    static final long TEST_MP3_EXTRA_FILE_DURATION = 252L;
    static final long TEST_MP3_EXTRA_FILE_SIZE = 10218853;
    static final long TEST_MP3_EXTRA_FILE_BITRATE = 320;

    static final String TEST_MP3_ME_FILE_NAME =
            UnitTestConfiguration.getFullTestFilesPath("sample_mp3_6.mp3");
    static final long TEST_MP3_ME_FILE_DURATION = 278L;
    static final long TEST_MP3_ME_FILE_SIZE = 11456458;
    static final long TEST_MP3_ME_FILE_BITRATE = 320;

    static final String TEST_VOB_FILE_NAME =
            UnitTestConfiguration.getFullTestFilesPath("sample_960x400_ocean_with_audio.vob");
    static final long TEST_VOB_FILE_DURATION = 47L;
    static final long TEST_VOB_FILE_SIZE = 8615936L;
    static final long TEST_VOB_WIDTH = 960L;
    static final long TEST_VOB_HEIGHT = 400L;
}
