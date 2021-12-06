package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Account {

    private String username, password, email, joined;

    private int id, totalDownloads;
}