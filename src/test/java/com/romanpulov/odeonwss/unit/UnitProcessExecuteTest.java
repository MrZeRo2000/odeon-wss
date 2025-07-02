package com.romanpulov.odeonwss.unit;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UnitProcessExecuteTest {
    private final static String FFPROBE_PATH = "../ffmpeg/ffprobe.exe";

    private final String TEST_FILE_PATH = UnitTestConfiguration.getTestFilesPath();

    @Test
    void test() throws Exception {
        Process process = new ProcessBuilder()
                .command(FFPROBE_PATH, "-print_format", "json", "-show_format", "-v", "quiet",
                        String.format("%s/sample_mp3_1.mp3", TEST_FILE_PATH)
                )
                .start();

        String text = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        String errorText = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));


        assertThat(text.isEmpty()).isFalse();
        assertThat(errorText.isEmpty()).isTrue();

        System.out.println("Text:" + text);
        System.out.println("Error Text:" + errorText);
    }

    @Test
    void testSequence() throws Exception {
        Path path = Paths.get(TEST_FILE_PATH);
        try (Stream<Path> stream = Files.list(path)) {
            List<Path> files = stream.filter(p -> p.toString().endsWith("mp3")).toList();
            assertThat(files.isEmpty()).isFalse();

            for (Path file: files) {
                Process process = new ProcessBuilder()
                        .command(FFPROBE_PATH, "-print_format", "json", "-show_format", "-v", "quiet", file.toAbsolutePath().toString()
                        )
                        .start();

                String text = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                assertThat(text.isEmpty()).isFalse();

                String errorText = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                assertThat(errorText.isEmpty()).isTrue();

                System.out.println(file.getFileName().toString() + ":" + text);
                System.out.println(file.getFileName().toString() + ":" + errorText);
            }
        }
    }

    @Test
    void testParallel() throws Exception {
        List<Callable<String>> callables = new ArrayList<>();
        Path path = Paths.get(TEST_FILE_PATH);
        try (Stream<Path> stream = Files.list(path)) {
            List<Path> files = stream.toList();
            for (Path file: files) {
                Callable<String> callable = () -> {

                    Process process = new ProcessBuilder()
                            .command(FFPROBE_PATH, "-print_format", "json", "-show_format", "-show_streams", "-v", "quiet", file.toAbsolutePath().toString()
                            )
                            .start();

                    String text = new BufferedReader(
                            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining());

                    String errorText = new BufferedReader(
                            new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining());

                    if (!text.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(text);
                        JSONArray streamsArray = jsonObject.optJSONArray("streams");
                        JSONObject formatObject = jsonObject.optJSONObject("format");
                        if (streamsArray == null) {
                            System.out.println("Streams not found");
                        }
                        if (formatObject == null) {
                            System.out.println("Format not found");
                        }
                    }

                    System.out.println(file.getFileName().toString() + ":" + text);
                    System.out.println(file.getFileName().toString() + ":" + errorText);

                    return file.getFileName().toString();
                };
                callables.add(callable);
            }
        }

        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            List<Future<String>> futures = executorService.invokeAll(callables);

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }

            for (Future<String>f : futures ) {
                System.out.println("Future:" + f.get());
            }
        }
    }
}
