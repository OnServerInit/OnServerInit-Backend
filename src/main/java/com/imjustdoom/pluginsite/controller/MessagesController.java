package com.imjustdoom.pluginsite.controller;

import com.google.gson.Gson;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Message;
import com.imjustdoom.pluginsite.model.MessageGroup;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.MessageGroupRepository;
import com.imjustdoom.pluginsite.repositories.MessageRepository;
import com.imjustdoom.pluginsite.util.RequestUtil;
import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class MessagesController {

    private final MessageRepository messageRepository;
    private final MessageGroupRepository messageGroupRepository;
    private final AccountRepository accountRepository;

    private void sendSystemMessage(String message, int groupId){
//        Account systemAccount = new Account("System", "a@a.a", "a@a.a");
//        accountRepository.save(systemAccount);
        Account systemAccount = accountRepository.findByUsernameEqualsIgnoreCase("System").get();
        MessageGroup messageGroup = messageGroupRepository.findById(groupId).get();
        Message systemMessage = new Message(message, systemAccount, messageGroup);
        messageRepository.save(systemMessage);
    }

    @GetMapping("/message")
    public String message(Model model, Account account){
        if (account == null) {
            return "redirect:/login";
        }
        model.addAttribute("account", account);
        return "message/message";
    }

    @GetMapping("/message/new/{id}")
    public String newMessage(Model model, Account account, @PathVariable("id") int id){
        List<Account> users = new ArrayList<>();
        Optional<Account> adding = accountRepository.findById(id);
        // TODO: add an error message
        if(adding.isEmpty() || adding.get().getId() == account.getId()) return "redirect:/message";
        users.add(adding.get());
        users.add(account);
        String name = "Generic Group #" + (int) (Math.random() * 100);
        MessageGroup messageGroup = new MessageGroup(name, LocalDateTime.now(), users);
        messageGroupRepository.save(messageGroup);

        return "redirect:/message";
    }

    @PostMapping(value = "/message/api/get_groups", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getConversations(Account account){
        List<MessageGroup> messageGroups;
        if(account == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        messageGroups = account.getGroups();
        HashMap<String, String> response = new HashMap<>();
        for (MessageGroup messageGroup : messageGroups) {
            HashMap<String, String> group = new HashMap<>();
            group.put("GroupId", String.valueOf(messageGroup.getId()));
            group.put("GroupName", messageGroup.getName());
            group.put("GroupMembers", String.valueOf(messageGroup.getMembers().size()));
            response.put("Group." + response.size(), new Gson().toJson(group));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/message/api/get_group", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getGroup(@RequestBody String request) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        HashMap<String, String> response = new HashMap<>();
        for (Account account : messageGroup.getMembers()) {
            response.put("Username." + response.size(), account.getUsername());
        }
        response.put("GroupName", messageGroup.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/message/api/get_messages", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getMessages(@RequestBody String request, Account account) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        List<Message> messages = messageGroup.getMessages();
        HashMap<String, String> response = new HashMap<>();
        for (Message message : messages) {
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("MessageId", String.valueOf(message.getId()));
            if(message.getAuthor().getUsername().equalsIgnoreCase("System")) {
                messageMap.put("MessageAuthor", "system");
            }else{
                messageMap.put("MessageAuthor", (message.getAuthor().getId() == account.getId()) ? "self" : "other");
            }
            messageMap.put("MessageAuthorId", String.valueOf(message.getAuthor().getId()));
            messageMap.put("MessageAuthorName", message.getAuthor().getUsername());
            messageMap.put("MessageContent", message.getContent());

            response.put("Message." + response.size(), new Gson().toJson(messageMap));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/message/api/send_message")
    public ResponseEntity<HashMap<String, String>> sendMessage(@RequestBody String request, Account account) {
        // send message to that conversation
        // return json
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        String content = params.get("content");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        Message message = new Message(content, account, messageGroup);
        messageRepository.save(message);
        HashMap<String, String> response = new HashMap<>();
        response.put("MessageId", String.valueOf(message.getId()));
        response.put("MessageAuthor", String.valueOf(message.getAuthor().getId()));
        response.put("MessageContent", message.getContent());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/message/api/create_group", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getGroups(@RequestBody String request, Account account) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String userIds = params.get("userIds");
        String name = "Generic Group #" + (int) (Math.random() * 100);
        String[] userIdsList = userIds.split("%2C");
        List<Account> users = new ArrayList<Account>();
        for (String userId : userIdsList) {
            users.add(accountRepository.findById(Integer.parseInt(userId)).get());
        }

        users.add(account);

        MessageGroup messageGroup = new MessageGroup(name, LocalDateTime.now(), users);
        messageGroupRepository.save(messageGroup);

        HashMap<String, String> response = new HashMap<>();
        response.put("groupId", String.valueOf(messageGroup.getId()));
        response.put("name", messageGroup.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "message/api/add_member", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> addMember(@RequestBody String request, Account account) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        String userId = params.get("userId");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        Account user = accountRepository.findByUsernameEqualsIgnoreCase(userId).get();
        messageGroup.getMembers().add(user);
        messageGroupRepository.save(messageGroup);
        HashMap<String, String> response = new HashMap<>();
        response.put("groupId", groupId);
        sendSystemMessage("User " + user.getUsername() + " has been added to the group.", messageGroup.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "message/api/leave", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> leave(@RequestBody String request, Account account) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        messageGroup.getMembers().remove(account);
        messageGroupRepository.save(messageGroup);
        HashMap<String, String> response = new HashMap<>();
        response.put("groupId", groupId);
        sendSystemMessage("User " + account.getUsername() + " has left the group.", messageGroup.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "message/api/delete_group", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> deleteGroup(@RequestBody String request, Account account) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        messageGroupRepository.delete(messageGroup);
        HashMap<String, String> response = new HashMap<>();
        response.put("groupId", groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "message/api/edit_group_name", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> editGroupName(@RequestBody String request, Account account) throws UnsupportedEncodingException {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        String name = params.get("name");
        name = URLDecoder.decode(name, StandardCharsets.UTF_8);
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        messageGroup.setName(name);
        messageGroupRepository.save(messageGroup);
        HashMap<String, String> response = new HashMap<>();
        response.put("groupId", groupId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "message/api/fuzzy_search/users", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> searchUsers(@RequestBody String request, Account account) {
        HashMap<String, String> params = RequestUtil.getParams(request);
        String searchTerm = params.get("searchTerm");

        List<BoundExtractedResult<Account>> searchResults = FuzzySearch.extractSorted(searchTerm, accountRepository.findAll(), Account::getUsername);

        HashMap<String, String> response = new HashMap<>();
        List<String> usernames = new ArrayList<>();
        for(BoundExtractedResult<Account> result : searchResults) {
            usernames.add(result.getString());
        }
        response.put("usernames", String.join(",", usernames));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
