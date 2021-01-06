package me.bleaf.comix.admin.app.librarian;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bleaf.comix.admin.configuration.ComixConfgPropertyName;
import me.bleaf.comix.admin.configuration.ComixConfig;
import me.bleaf.comix.admin.job.ComixJob;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComixPaths {
    final ComixFinder comixFinder;
    final ComixConfig comixConfig;

    public List<Path> getAllPathList(String root) {
        log.info("get all paht, root = {}", root);

        List<Path> jobPathList;
        try {
            String exclusion = comixConfig.getString(ComixConfgPropertyName.COMIX_EXCLUSION_DIRS);

            jobPathList = Files.list(Paths.get(root))
                    .filter(Files::isDirectory)
                    .filter(p -> !p.getFileName().toString().equals(exclusion))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("error get list in comix path = {}, \ncause = {}", root, e.getCause());
            throw new RuntimeException(e);
        }

        if (jobPathList == null || jobPathList.isEmpty()) {
            log.info(" ### main list is empty");
            return null;
        }

        log.info(" #1. main list count = {}, read comix list.. = {}", jobPathList.size(), jobPathList);

        int numThread = comixConfig.getInt(ComixConfgPropertyName.ALL_COMIX_PATH_THREAD_NUM);

        List<Path> jobList = new ArrayList<>();
        List<CompletableFuture<List<Path>>> completableFutureList = new ArrayList<>();
        for (Path path : jobPathList) {
            completableFutureList.add(comixFinder.findComixPath(path));
            log.debug("find directory = {}", path.getFileName());

            if(completableFutureList.size() == numThread) {
                jobList.addAll(completableFutureList.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));

                completableFutureList = new ArrayList<>();
            }
        }

        if(completableFutureList.size() > 0) {
            jobList.addAll(completableFutureList.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
        }

        return jobList;
    }
}
