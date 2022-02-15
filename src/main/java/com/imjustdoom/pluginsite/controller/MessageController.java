package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public void sendMessage(Account account, @RequestParam int groupId, @RequestParam String content) throws RestException {
        this.messageService.sendMessage(account, groupId, content);
    }
}
