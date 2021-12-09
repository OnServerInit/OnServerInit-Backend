package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Update {

    private String filename, description, versions, download, name, uploaded;

    private int fileId;
}
