package com.romanpulov.odeonwss.generator;

import org.json.JSONObject;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileTreeGenerator {
    public record FolderDef(String folderName, Map<String, Path> files) {}

    public static void generate(Path rootFolder, Collection<FolderDef> folderDefs) throws IOException {
        if (!Files.exists(rootFolder)) {
            throw new IOException("Root folder does not exist: " + rootFolder);
        }
        for (FolderDef folderDef : folderDefs) {
            Path folderPath = Path.of(rootFolder.toString(), folderDef.folderName);
            Files.deleteIfExists(folderPath);
            Files.createDirectory(folderPath);

            for (Map.Entry<String, Path> file: folderDef.files.entrySet()) {
                Path sourcePath = file.getValue();
                if (!Files.exists(sourcePath)) {
                    throw new IOException(
                            "Source file does not exist for folder " +
                                    folderDef.folderName + " : " +
                                    sourcePath);
                }

                Path targetPath = Path.of(folderPath.toString(), file.getKey());
                Files.copy(file.getValue(), targetPath, REPLACE_EXISTING);
            }
        }
    }

    private static void processJSONKey(Path path, String testDataPath, JSONObject jo, String key) throws IOException {
        if (key == null) {
            jo.keySet().forEach(v -> {
                try {
                    processJSONKey(path, testDataPath, jo, v);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            Object ko = jo.get(key);
            Path keyPath = Path.of(path.toString(), key);
            if (ko instanceof String keyValue) {
                Path sourcePath = Path.of(testDataPath, keyValue);
                if (!Files.exists(sourcePath)) {
                    throw new IOException("Source file not found:" + sourcePath);
                } else {
                    Files.copy(sourcePath, keyPath, REPLACE_EXISTING);
                }
            } else if (ko instanceof JSONObject) {
                Files.createDirectory(keyPath);
                processJSONKey(keyPath, testDataPath, (JSONObject) ko, null);
            }
        }
    }

    public static void generateFromJSON(Path rootFolder, String testDataPath, String json) throws IOException {
        if (!Files.exists(rootFolder)) {
            throw new IOException("Root folder does not exist: " + rootFolder);
        }
        JSONObject jo = new JSONObject(json);
        processJSONKey(rootFolder, testDataPath, jo, null);
    }

    public static Map<String, Path> generateTempFileTreeNames(Collection<String> names) {
        return names
                .stream()
                .collect(Collectors.toMap(v -> v, v -> {
                    try {
                        return Files.createTempDirectory(v);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    public static void deleteTempFiles(Collection<Path> paths) {
        paths.forEach(v -> {
            try {
                FileSystemUtils.deleteRecursively(v);
            } catch (IOException ignore) {}
        });
    }

    public static Path getTempFolder(String folderName) {
        String tempDirPath = System.getProperty("java.io.tmpdir");
        return Paths.get(tempDirPath, folderName);
    }

    public static <E extends Enum<E>> Map<E, Path> createTempFolders(Class<E> enumClass) {
        return EnumSet
                .allOf(enumClass).stream()
                .collect(Collectors.toMap(v -> v, v -> {
                    try {
                        return Files.createTempDirectory(String.valueOf(v));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }
}
