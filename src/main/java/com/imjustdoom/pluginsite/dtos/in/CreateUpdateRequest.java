package com.imjustdoom.pluginsite.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CreateUpdateRequest {

    private String name;
    private String version;
    private String externalLink;
    private String description;
}