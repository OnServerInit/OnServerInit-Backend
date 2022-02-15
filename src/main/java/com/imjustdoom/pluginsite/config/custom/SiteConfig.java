package com.imjustdoom.pluginsite.config.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@Getter
@ConfigurationProperties("app")
@ConstructorBinding
@AllArgsConstructor
public class SiteConfig {

    private final String domain;
    private final int maxUpdatesPerHour;
    private final int maxCreationsPerHour;

    @DataSizeUnit(DataUnit.BYTES)
    private final DataSize maxUploadSize;
}
