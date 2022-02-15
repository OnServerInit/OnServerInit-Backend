package com.imjustdoom.pluginsite.dtos.out.message;

import com.imjustdoom.pluginsite.model.MessageGroup;

import java.time.LocalDateTime;

public record MessageGroupDto(int id, String name, LocalDateTime createdTime) {

    public static MessageGroupDto fromMessageGroup(MessageGroup group) {
        return new MessageGroupDto(group.getId(), group.getName(), group.getCreatedTime());
    }
}
