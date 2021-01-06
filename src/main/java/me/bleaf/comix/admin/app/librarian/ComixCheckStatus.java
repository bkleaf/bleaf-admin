package me.bleaf.comix.admin.app.librarian;

public enum ComixCheckStatus {
    EMPTY, MIX, ZERO_FILE, MIX_ZERO_FILE, NOT_COMPRESS, ZERO_NOT_COMPRESS, NONE;

    public static ComixCheckStatus check(boolean hasDirectory, boolean zeroSize, boolean isImage) {
        if(hasDirectory && zeroSize) {
            return MIX_ZERO_FILE;
        } else if(zeroSize && isImage) {
            return ZERO_NOT_COMPRESS;
        } else if(hasDirectory) {
            return MIX;
        } else if(zeroSize) {
            return ZERO_FILE;
        } else if(isImage) {
            return NOT_COMPRESS;
        }

        return NONE;
    }
}
