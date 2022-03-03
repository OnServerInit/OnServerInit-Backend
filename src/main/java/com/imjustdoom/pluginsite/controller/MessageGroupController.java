package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.out.message.MessageDto;
import com.imjustdoom.pluginsite.dtos.out.message.MessageGroupDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message/group")
@RequiredArgsConstructor
public class MessageGroupController {
    private final MessageService messageService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void createGroup(Account sender, @RequestParam int targetId) throws RestException {
        this.messageService.createGroup(sender, targetId);
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public MessageGroupDto getGroup(Account account, @PathVariable int groupId) throws RestException {
        return MessageGroupDto.fromMessageGroup(this.messageService.getGroup(account, groupId));
    }

    @GetMapping("/{groupId}/messages")
    @PreAuthorize("isAuthenticated()")
    public Page<MessageDto> getMessages(Account account, @PathVariable int groupId, @PageableDefault(size = 25) Pageable pageable) throws RestException {
        return this.messageService.getMessages(account, groupId, pageable)
            .map(MessageDto::fromMessage);
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public void deleteGroup(Account account, @PathVariable int groupId) throws RestException {
        this.messageService.deleteGroup(account, groupId);
    }

    @PostMapping("/{groupId}/addUser")
    @PreAuthorize("isAuthenticated()")
    public void addUserToGroup(Account account, @PathVariable int groupId, @RequestParam int targetId) throws RestException {
        this.messageService.addUserToGroup(account, groupId, targetId);
    }

    @PostMapping("/{groupId}/leave")
    @PreAuthorize("isAuthenticated()")
    public void leaveGroup(Account account, @PathVariable int groupId) throws RestException {
        this.messageService.leaveGroup(account, groupId);
    }

    @PatchMapping("/{groupId}/name")
    @PreAuthorize("isAuthenticated()")
    public MessageGroupDto editGroupName(Account account, @PathVariable int groupId, @RequestParam String name) throws RestException {
        return MessageGroupDto.fromMessageGroup(this.messageService.editGroupName(account, groupId, name));
    }

}
