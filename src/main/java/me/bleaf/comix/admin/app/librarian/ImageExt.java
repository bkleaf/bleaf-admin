package me.bleaf.comix.admin.app.librarian;

import java.util.stream.Stream;

public enum ImageExt {
    jpg, jpeg, png, gif;

    public static boolean isImage(String ext) {
        return Stream.of(ImageExt.values()).anyMatch(ie -> ie.name().equals(ext));
    }
}
