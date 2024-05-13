package com.romanpulov.odeonwss.generator;

import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
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

    public static void deleteTempFiles(Map<String, Path> tempFileTreeNames) {
        tempFileTreeNames.values().forEach(v -> {
            try {
                FileSystemUtils.deleteRecursively(v);
            } catch (IOException ignore) {}
        });
    }
}
