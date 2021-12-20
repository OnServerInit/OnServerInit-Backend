package com.imjustdoom.pluginsite;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PluginSiteApplication {

    public static void main(String[] args) throws IOException, SQLException {
        SpringApplication.run(PluginSiteApplication.class, args);
    }
}