package me.bleaf.comix.admin;

import lombok.extern.slf4j.Slf4j;
import me.bleaf.comix.admin.job.ArrangeAllComix;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class BleafComixAdminApplicationTests {
    @Autowired
    ArrangeAllComix arrangeAllComix;

    @Test
    void contextLoads() throws IOException {
        arrangeAllComix.work();

        Path file = Paths.get("Z:\\_미분류\\manamoa\\그래도 아유무는 다가온다\\그래도 아유무는 다가온다 1화.zip");
        File file2 = new File("Z:\\_미분류\\manamoa\\그래도 아유무는 다가온다\\그래도 아유무는 다가온다 1화.zip");

        System.out.println(Files.size(file) + ", " + file2.length() + ", " + file.getFileName().toString() + ", " + com.google.common.io.Files.getFileExtension(file.getFileName().toString()));

        Path file3 = Paths.get("Z:\\_성인\\mix_dir\\test1");
        List<Path> list = Files.list(file3).collect(Collectors.toList());

        System.out.println("sub size = " + list.size() + ", title = " + file3.getFileName().toString());
    }

}
