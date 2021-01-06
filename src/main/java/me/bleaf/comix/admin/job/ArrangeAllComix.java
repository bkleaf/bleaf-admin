package me.bleaf.comix.admin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bleaf.comix.admin.app.librarian.ComixFinder;
import me.bleaf.comix.admin.app.librarian.ComixPaths;
import me.bleaf.comix.admin.configuration.ComixConfgPropertyName;
import me.bleaf.comix.admin.configuration.ComixConfig;
import me.bleaf.comix.admin.dao.ComixCheckDao;
import me.bleaf.comix.admin.dao.ComixFilesDao;
import me.bleaf.comix.admin.model.Comix;
import me.bleaf.comix.admin.dao.ComixDao;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrangeAllComix implements ComixJob{
    final ComixConfig comixConfig;
    final ComixPaths comixPaths;
    final ComixFinder comixFinder;
    final ComixDao comixDao;
    final ComixCheckDao comixCheckDao;
    final ComixFilesDao comixFilesDao;

    @PostConstruct
    @Override
    public void work() {
        List<Path> allComixPath = comixPaths.getAllPathList(comixConfig.getString(ComixConfgPropertyName.ROOT_PATH));

        log.info("all path = {}, count = {}", comixConfig.getString(ComixConfgPropertyName.ROOT_PATH), allComixPath.size());

        int threadNum = comixConfig.getInt(ComixConfgPropertyName.ALL_COMIX_PATH_THREAD_NUM);
        int saveNum = comixConfig.getInt(ComixConfgPropertyName.COMIX_SAVE_NUM);

        int totalCount = 0, saveCount = 0, checkCount = 0;
        List<Comix> comixList = new ArrayList<>();
        List<CompletableFuture<Comix>> completableFutureList = new ArrayList<>();
        for(Path p : allComixPath) {
            completableFutureList.add(comixFinder.findComix(p));

            if(completableFutureList.size() == threadNum) {
                comixList.addAll(completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
                completableFutureList = new ArrayList<>();
            }
            
            if(comixList.size() == saveNum) {
                Map<Boolean, List<Comix>> checkComixMap = comixList.stream()
                        .collect(Collectors.groupingBy(Comix::isCheck));

                List<Comix> saveList = checkComixMap.get(false);
                comixDao.saveAll(saveList);
                comixFilesDao.saveAll(saveList);

                List<Comix> checkList = checkComixMap.get(true);
                comixCheckDao.saveAll(checkList);

                saveCount += saveList.size();
                checkCount += checkList.size();

                totalCount += comixList.size();
                log.info("save comix count, save, check, total = {}, {}, {}, {}", comixList.size(), saveList.size(), checkList.size(), totalCount);

                comixList = new ArrayList<>();
            }
        }

        if(completableFutureList.size() > 0) {
            comixList.addAll(completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        }

        if(comixList.size() > 0) {
            Map<Boolean, List<Comix>> checkComixMap = comixList.stream()
                    .collect(Collectors.groupingBy(Comix::isCheck));

            List<Comix> saveList = checkComixMap.get(false);
            comixDao.saveAll(saveList);
            comixFilesDao.saveAll(saveList);

            List<Comix> checkList = checkComixMap.get(true);
            comixCheckDao.saveAll(checkList);

            saveCount += saveList.size();
            checkCount += checkList.size();

            totalCount += comixList.size();

            log.info("last save comix count, save, check, total = {}, {}, {}, {}", comixList.size(), saveList.size(), checkList.size(), totalCount);
        }

        log.info("total save count, save, check = {}, {}, {}", totalCount, saveCount, checkCount);
    }
}
