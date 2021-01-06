package me.bleaf.comix.admin.model;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class ComixFile {
    String comixTitle;
    Path comixPath;
    String fileName;
    Path filePath;
    String fileExt;
    long fileSize;
}
