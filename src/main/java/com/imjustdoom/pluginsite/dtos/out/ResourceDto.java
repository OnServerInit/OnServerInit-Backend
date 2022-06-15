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
public class ResourceDto {

    private int id;
    private final int totalDownloads;
    private final String name;
    private final String description;
    private final String blurb;
    private final LocalDateTime created;
    private final LocalDateTime updated;
    private final String donation;
    private final String source;
    private final String support;
    private final String category;
    private final String status;
    private final byte[] logo;
    private final String account;

    private final List<UpdateDto> updates;

    public static ResourceDto create(Resource resource, int totalDownloads) {
        return new ResourceDto(resource.getId(), totalDownloads, resource.getName(), resource.getDescription(), resource.getBlurb(),
                resource.getCreated(), resource.getUpdated(), resource.getDonation(), resource.getSource(),
                resource.getSupport(), resource.getCategory(), resource.getStatus(), resource.getLogo(),
                "test", getUpdates(resource.getUpdates()));
    }

    private static List<UpdateDto> getUpdates(List<Update> updates) {
        List<UpdateDto> updateDtos = new ArrayList<>();
        for(Update update : updates) {
            updateDtos.add(UpdateDto.create(update));
        }
        return updateDtos;
    }
}
