package com.imjustdoom.pluginsite.storage;

import com.imjustdoom.pluginsite.util.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageManager {

    private final Path root = Paths.get("resources");

    public void init() {
        if (!FileUtil.doesFileExist("./resources")) {
            try {
                Files.createDirectory(Paths.get("./resources"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!FileUtil.doesFileExist("./resources/plugins")) {
            try {
                Files.createDirectory(Paths.get("./resources/plugins"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!FileUtil.doesFileExist("./resources/logos")) {
            try {
                Files.createDirectory(Paths.get("./resources/logos"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void storeFile(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}
