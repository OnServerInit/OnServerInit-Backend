package com.imjustdoom.pluginsite.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    /**
     * Checks if a file exists
     * @param filename - Name of the file/directory
     * @return - Returns if the file/directory exists
     */
    public static boolean doesFileExist(String filename) {
        return Files.exists(Path.of(filename));
    }
}
