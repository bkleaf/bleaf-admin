package me.bleaf.comix.admin.app.librarian;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.bleaf.comix.admin.model.Comix;
import me.bleaf.comix.admin.model.ComixFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Slf4j
@Component
public class ComixFinder {
    public CompletableFuture<List<Path>> findComixPath(Path root) {
        List<Path> comixDirectoryList = null;

        try (Stream<Path> paths = Files.walk(root)) {
            comixDirectoryList = paths.filter(Files::isDirectory)
                    .filter(p -> {
                        File[] files = p.toFile().listFiles();
                        if (Stream.of(files).anyMatch(File::isFile)) {
                            return true;
                        }

                        if (Stream.of(files).anyMatch(File::isDirectory)) {
                            return false;
                        }

                        return true;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(comixDirectoryList);
    }

    public CompletableFuture<Comix> findComix(Path root) {
        String title = root.getFileName().toString();

        List<Path> subList;
        try {
            subList = Files.list(root).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("error get sub file list, root = {}, cause = \n{}", root.toString(), e.getCause());
            throw new RuntimeException(e);
        }

        if (subList.isEmpty()) {
            return CompletableFuture.completedFuture(Comix.builder()
                    .title(title)
                    .comixPath(root)
                    .check(true)
                    .status(ComixCheckStatus.EMPTY)
                    .build());
        }

        boolean check = false, hasSubDirectory = false, zeroFile = false, isImage = false;
        List<ComixFile> comixFileList = new ArrayList<>();
        for (Path sub : subList) {
            if (Files.isRegularFile(sub)) {
                String fileName = sub.getFileName().toString();
                String fileExt = com.google.common.io.Files.getFileExtension(fileName).toLowerCase();

                long fileSize;
                try {
                    fileSize = Files.size(sub);
                } catch (IOException e) {
                    log.error("error get file size, file = {}, cause = \n{}", sub.toString(), e.getCause());
                    throw new RuntimeException(e);
                }

                if (ImageExt.isImage(fileExt)) {
                    isImage = true;

                    if (fileSize <= 10) {
                        zeroFile = true;
                    }
                    check = true;
                } else if (fileSize <= 1024) {
                    zeroFile = true;
                    check = true;
                }

                comixFileList.add(ComixFile.builder()
                        .comixTitle(title)
                        .comixPath(root)
                        .fileName(fileName)
                        .filePath(sub)
                        .fileExt(fileExt)
                        .fileSize(fileSize)
                        .build());

            } else {
                hasSubDirectory = true;
                check = true;
            }
        }

        return CompletableFuture.completedFuture(Comix.builder()
                .title(title)
                .comixPath(root)
                .volume(comixFileList.size())
                .comixFileList(comixFileList)
                .check(check)
                .status(ComixCheckStatus.check(hasSubDirectory, zeroFile, isImage))
                .build());
    }
}


