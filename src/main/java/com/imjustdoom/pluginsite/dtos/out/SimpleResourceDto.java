package com.imjustdoom.pluginsite.dtos.out;

import com.imjustdoom.pluginsite.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SimpleResourceDto {

    private int id;
    private final String name;
    private final String blurb;
    private final LocalDateTime created;
    private final LocalDateTime updated;

    private final int totalDownloads;

    public static SimpleResourceDto create(Resource resource, int totalDownloads) {
        return new SimpleResourceDto(resource.getId(), resource.getName(), resource.getBlurb(), resource.getCreated(), resource.getUpdated(), totalDownloads);
    }
}
