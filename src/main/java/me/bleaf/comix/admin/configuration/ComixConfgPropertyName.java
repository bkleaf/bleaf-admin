package me.bleaf.comix.admin.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComixConfgPropertyName {
    ROOT_PATH("root_path"),
    ALL_COMIX_PATH_THREAD_NUM("all_comix_path_thread_num"),
    COMIX_SAVE_NUM("comix_save_num"),
    COMIX_EXCLUSION_DIRS("comix_exclusion_dirs");

    @Getter
    final String propertyName;
}
