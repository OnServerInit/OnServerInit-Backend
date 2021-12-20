package com.imjustdoom.pluginsite.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateResourceRequest {

    private String name;
    private String blurb;
    private String donation;
    private String source;
    private String support;
    private String description;
    private String category;
}