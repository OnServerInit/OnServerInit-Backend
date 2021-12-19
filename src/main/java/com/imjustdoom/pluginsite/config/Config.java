package com.imjustdoom.pluginsite.config;

import lombok.Getter;

@Getter
public class Config {

    public String username;
    public String password;
    public String host;
    public String port;
    public String database;
    public int maxUploadSizeByte;
    public String domain;
    public int maxCreationsPerHour;
    public int maxUpdatesPerHour;
}
