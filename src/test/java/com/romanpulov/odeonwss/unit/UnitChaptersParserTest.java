package com.romanpulov.odeonwss.unit;

import com.romanpulov.odeonwss.utils.media.ChaptersParser;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import java.util.logging.Logger;

public class UnitChaptersParserTest {
    Logger logger = Logger.getLogger(getClass().getSimpleName());

    @Test
    void test2ChaptersFracSmall() throws Exception {
        var chapters = List.of(
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:06:28.160",
                "CHAPTER02NAME=Chapter 02"
        );
        var result = ChaptersParser.parseLines("Test", chapters);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.iterator().next()).isEqualTo(6 * 60 + 28);
    }

    @Test
    void test2ChaptersFracBig() throws Exception {
        var chapters = List.of(
                "CHAPTER01=00:00:00.000",
                "CHAPTER01NAME=Chapter 01",
                "CHAPTER02=00:23:34.960",
                "CHAPTER02NAME=Chapter 02"
        );
        var result = ChaptersParser.parseLines("Test", chapters);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.iterator().next()).isEqualTo(23 * 60 + 34 + 1);
    }

    @Test
    void testFull() throws Exception {
        var chapters = new String[] {
            "CHAPTER01=00:00:00.000",
            "CHAPTER01NAME=Chapter 01",
            "CHAPTER02=00:06:28.160",
            "CHAPTER02NAME=Chapter 02",
            "CHAPTER03=00:12:26.160",
            "CHAPTER03NAME=Chapter 03",
            "CHAPTER04=00:19:30.160",
            "CHAPTER04NAME=Chapter 04",
            "CHAPTER05=00:26:18.080",
            "CHAPTER05NAME=Chapter 05",
            "CHAPTER06=00:32:51.240",
            "CHAPTER06NAME=Chapter 06",
            "CHAPTER07=00:39:25.080",
            "CHAPTER07NAME=Chapter 07",
            "CHAPTER08=00:45:33.080",
            "CHAPTER08NAME=Chapter 08",
            "CHAPTER09=00:52:00.000",
            "CHAPTER09NAME=Chapter 09",
            "CHAPTER10=00:58:22.280",
            "CHAPTER10NAME=Chapter 10",
            "CHAPTER11=01:04:46.120",
            "CHAPTER11NAME=Chapter 11",
            "CHAPTER12=01:10:57.960",
            "CHAPTER12NAME=Chapter 12",
            "CHAPTER13=01:17:18.040",
            "CHAPTER13NAME=Chapter 13",
            "CHAPTER14=01:23:34.960",
            "CHAPTER14NAME=Chapter 14"
        };
        var result = ChaptersParser.parseLines("Test", Lists.list(chapters));
        assertThat(result.size()).isEqualTo(13);

        var iterator = result.iterator();
        assertThat(iterator.next()).isEqualTo(6 * 60 + 28);
        assertThat(iterator.next()).isEqualTo((12 - 6) * 60 + (26 - 28));
        assertThat(iterator.next()).isEqualTo((19 - 12) * 60 + (30 - 26));
        assertThat(iterator.next()).isEqualTo((26 - 19) * 60 + (18 - 30));
        assertThat(iterator.next()).isEqualTo((32 - 26) * 60 + (51 - 18));
        assertThat(iterator.next()).isEqualTo((39 - 32) * 60 + (25 - 51));
        assertThat(iterator.next()).isEqualTo((45 - 39) * 60 + (33 - 25));
        assertThat(iterator.next()).isEqualTo((52 - 45) * 60 + (00 - 33));
        assertThat(iterator.next()).isEqualTo((58 - 52) * 60 + (22 - 00));
        assertThat(iterator.next()).isEqualTo(60 * 60 + (4 - 58) * 60 + (46 - 22));
        assertThat(iterator.next()).isEqualTo((10 - 04) * 60 + (58 - 46));
        assertThat(iterator.next()).isEqualTo((17 - 10) * 60 + (18 - 58));
        assertThat(iterator.next()).isEqualTo((23 - 17) * 60 + (35 - 18));
    }
}
