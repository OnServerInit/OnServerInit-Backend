package com.imjustdoom.pluginsite;

import com.google.gson.Gson;
import com.imjustdoom.pluginsite.config.Config;
import com.imjustdoom.pluginsite.storage.StorageManager;
import com.imjustdoom.pluginsite.util.FileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

@SpringBootApplication
public class PluginSiteApplication extends SpringBootServletInitializer {

    private static StorageManager storageManager;
    public static Config config;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PluginSiteApplication.class);
    }

    public static void main(String[] args) throws IOException, SQLException {

        // Load the config and create it if it doesn't exist
        if (!FileUtil.doesFileExist("config.json")) {
            InputStream stream = PluginSiteApplication.class.getResourceAsStream("/" + "config.json");
            assert stream != null;
            Files.copy(stream, Path.of("config.json"));
        }

        Gson gson = new Gson();

        String data = Files.readString(Path.of("config.json"));
        config = gson.fromJson(data, Config.class);

        storageManager = new StorageManager();

        // Load StorageManager
        storageManager.init();

        SpringApplication.run(PluginSiteApplication.class, args);
    }

    public static StorageManager getStorageManager() {
        return storageManager;
    }
}