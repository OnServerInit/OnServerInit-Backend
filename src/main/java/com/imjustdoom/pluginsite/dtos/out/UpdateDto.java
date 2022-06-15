package com.imjustdoom.pluginsite.dtos.out;

import com.imjustdoom.pluginsite.model.Update;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateDto(int id, String description, String filename, List<String> versions, List<String> software,
                        String downloadLink, String name, LocalDateTime uploaded, String version, int downloads,
                        String status) {

    public static UpdateDto create(Update update) {
        return new UpdateDto(update.getId(), update.getDescription(), update.getFilename(), update.getVersions(),
                update.getSoftware(), update.getDownloadLink(), update.getName(), update.getUploaded(),
                update.getVersion(), update.getDownloads(), update.getStatus());
    }
}
