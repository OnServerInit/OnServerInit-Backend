package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Setter
@Getter
public class ResourceFile {

    private String name, version, description, externalDownload;
    private int id;
}