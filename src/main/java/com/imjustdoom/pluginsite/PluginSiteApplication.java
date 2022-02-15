package com.imjustdoom.pluginsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@ConfigurationPropertiesScan
public class PluginSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(PluginSiteApplication.class, args);
    }
}