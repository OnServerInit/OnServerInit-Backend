package com.imjustdoom.pluginsite.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static boolean doesFileExist(String filename) {
        return Files.exists(Path.of(filename));
    }
}
