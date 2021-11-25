package com.imjustdoom.pluginsite.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Resource {

    private String name, description, blurb, created, updated, donation, source;

    private int id, downloads;
}