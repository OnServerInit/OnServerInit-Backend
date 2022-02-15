package com.imjustdoom.pluginsite.dtos.out.message;

import com.imjustdoom.pluginsite.model.Message;

public record MessageDto(int id, int authorId,
                         String content) {

    public static MessageDto fromMessage(Message message) {
        return new MessageDto(message.getId(), message.getAuthor().getId(), message.getContent());
    }
}
