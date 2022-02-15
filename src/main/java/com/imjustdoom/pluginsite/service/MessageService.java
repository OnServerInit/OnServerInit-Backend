package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Message;
import com.imjustdoom.pluginsite.model.MessageGroup;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.MessageGroupRepository;
import com.imjustdoom.pluginsite.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageGroupRepository messageGroupRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    private Account systemAccount;

    @PostConstruct
    public void setup() {
        this.systemAccount = this.accountRepository.findByUsernameEqualsIgnoreCase("system").orElseGet(() -> {
            Account account = new Account("system", "system@example.com", this.passwordEncoder.encode(UUID.randomUUID().toString())); // todo in the future, let's just lock the account
            return this.accountRepository.save(account);
        });
    }

    private void sendSystemMessage(int groupId, String message) throws RestException {
        MessageGroup messageGroup = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        Message systemMessage = new Message(message, this.systemAccount, messageGroup);
        this.messageRepository.save(systemMessage);
    }

    public void sendMessage(Account sender, int groupId, String content) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(sender)) throw new RestException(RestErrorCode.FORBIDDEN);

        Message message = new Message(content, sender, group);
        this.messageRepository.save(message);
    }

    public void createGroup(Account sender, int targetId) throws RestException {
        Account targetAccount = this.accountRepository.findById(targetId).orElseThrow(() -> new RestException(RestErrorCode.ACCOUNT_NOT_FOUND, "Target user not found"));

        List<Account> users = new ArrayList<>() {{
            add(sender);
            add(targetAccount);
        }};
        String name = "Generic Group #" + (int) (Math.random() * 100);
        MessageGroup messageGroup = new MessageGroup(name, LocalDateTime.now(), users);
        this.messageGroupRepository.save(messageGroup);
    }

    public Page<MessageGroup> getGroups(Account account, Pageable pageable) {
        return this.messageGroupRepository.findAllByMembersContains(account, pageable);
    }

    public MessageGroup getGroup(Account account, int groupId) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(account)) throw new RestException(RestErrorCode.FORBIDDEN);

        return group;
    }

    public void addUserToGroup(Account sender, int groupId, int targetId) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(sender)) throw new RestException(RestErrorCode.FORBIDDEN);

        Account target = this.accountRepository.findById(targetId).orElseThrow(() -> new RestException(RestErrorCode.ACCOUNT_NOT_FOUND, "Target user not found"));
        group.getMembers().add(target);
        this.messageGroupRepository.save(group);
    }

    public void leaveGroup(Account sender, int groupId) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(sender)) throw new RestException(RestErrorCode.FORBIDDEN);

        group.getMembers().remove(sender);
        this.messageGroupRepository.save(group);
    }

    public void deleteGroup(Account sender, int groupId) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(sender)) throw new RestException(RestErrorCode.FORBIDDEN);

        this.messageGroupRepository.delete(group);
    }

    public MessageGroup editGroupName(Account sender, int groupId, String newName) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(sender)) throw new RestException(RestErrorCode.FORBIDDEN);

        group.setName(newName);
        return this.messageGroupRepository.save(group);
    }

    public Page<Message> getMessages(Account account, int groupId, Pageable pageable) throws RestException {
        MessageGroup group = this.messageGroupRepository.findById(groupId).orElseThrow(() -> new RestException(RestErrorCode.MESSAGE_GROUP_NOT_FOUND));
        if (!group.getMembers().contains(account)) throw new RestException(RestErrorCode.FORBIDDEN);

        return this.messageRepository.findByGroup(group, pageable);
    }
}
