package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Setter
@Getter
public class Resource {

    private String name, description, blurb, created, updated, donation, source, download, author, support;

    private int id, downloads, authorid;
}