package com.romanpulov.odeonwss;


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

public class UnitProcessExecuteTest {
    private static String FFPROBE_PATH = "D:\\prj\\ffmpeg\\ffprobe.exe";

    @Test
    void test() throws Exception {
        Process process = new ProcessBuilder()
                .command(FFPROBE_PATH, "-print_format", "json", "-show_format", "-v", "quiet", "D:\\temp\\ok\\MP3 Music\\Aerosmith\\2004 Honkin'On Bobo\\01 - Road Runner.mp3"
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


        System.out.println("Text:" + text);
        System.out.println("Error Text:" + errorText);
    }

    @Test
    void testSequence() throws Exception {
        Path path = Paths.get("D:/temp/ok/MP3 Music/Aerosmith/2004 Honkin'On Bobo/");
        try (Stream<Path> stream = Files.list(path)) {
            List<Path> files = stream.collect(Collectors.toList());
            for (Path file: files) {
                Process process = new ProcessBuilder()
                        .command(FFPROBE_PATH, "-print_format", "json", "-show_format", "-v", "quiet", file.toAbsolutePath().toString()
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


                System.out.println(file.getFileName().toString() + ":" + text);
                System.out.println(file.getFileName().toString() + ":" + errorText);
            }
        }
    }

    @Test
    void testParallel() throws Exception {

        List<Callable<String>> callables = new ArrayList<>();
        Path path = Paths.get("D:/temp/ok/MP3 Music/Aerosmith/2004 Honkin'On Bobo/");
        try (Stream<Path> stream = Files.list(path)) {
            List<Path> files = stream.collect(Collectors.toList());
            for (Path file: files) {
                Callable<String> callable = () -> {

                    Process process = new ProcessBuilder()
                            .command(FFPROBE_PATH, "-print_format", "json", "-show_format", "-v", "quiet", file.toAbsolutePath().toString()
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


                    System.out.println(file.getFileName().toString() + ":" + text);
                    System.out.println(file.getFileName().toString() + ":" + errorText);

                    return file.getFileName().toString();
                };
                callables.add(callable);
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<String>> futures = executorService.invokeAll(callables);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
