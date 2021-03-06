package com.imjustdoom.pluginsite.dtos.out;

import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.model.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class SimpleResourceDto {

    private int id;
    private final String name;
    private final String blurb;
    private final LocalDateTime created;
    private final LocalDateTime updated;

    private final int totalDownloads;
    private final byte[] logo;

    private final List<UpdateDto> updates;

    public static SimpleResourceDto create(Resource resource, int totalDownloads) {
        return new SimpleResourceDto(resource.getId(), resource.getName(), resource.getBlurb(), resource.getCreated(), resource.getUpdated(), totalDownloads, resource.getLogo(),
                getUpdates(resource.getUpdates()));
    }

    private static List<UpdateDto> getUpdates(List<Update> updates) {
        List<UpdateDto> updateDtos = new ArrayList<>();
        for(Update update : updates) {
            updateDtos.add(UpdateDto.create(update));
        }
        return updateDtos;
    }
}
