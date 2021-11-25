package com.imjustdoom.pluginsite;

import com.google.gson.Gson;
import com.imjustdoom.pluginsite.config.Config;
import com.imjustdoom.pluginsite.database.PluginDatabase;
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

@SpringBootApplication
public class PluginSiteApplication extends SpringBootServletInitializer {

    private static PluginDatabase db;
    private static StorageManager storageManager;
    public static Config config;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PluginSiteApplication.class);
    }

    public static void main(String[] args) throws IOException {

        if(!FileUtil.doesFileExist("config.json")) {
            InputStream stream = PluginSiteApplication.class.getResourceAsStream("/" + "config.json");
            assert stream != null;
            Files.copy(stream, Path.of("config.json"));
        }

        Gson gson = new Gson();

        String data = Files.readString(Path.of("config.json"));
        config = gson.fromJson(data, Config.class);

        db = new PluginDatabase();
        storageManager = new StorageManager();

        db.init();
        storageManager.init();

        SpringApplication.run(PluginSiteApplication.class, args);
    }

    public static PluginDatabase getDB() {
        return db;
    }

    public static StorageManager getStorageManager() {
        return storageManager;
    }
}