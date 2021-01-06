package me.bleaf.comix.admin.model;


import lombok.Builder;
import lombok.Data;
import me.bleaf.comix.admin.app.librarian.ComixCheckStatus;

import java.nio.file.Path;
import java.util.List;

@Data
@Builder
public class Comix {
    String title;
    String author;
    int volume;
    String complete;

    Path comixPath;

    List<ComixFile> comixFileList;

    boolean check;
    ComixCheckStatus status;
}
