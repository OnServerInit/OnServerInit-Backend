package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.MessageGroup;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.MessageGroupRepository;
import com.imjustdoom.pluginsite.repositories.MessageRepository;
import com.imjustdoom.pluginsite.util.RequestUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@AllArgsConstructor
public class MessagesController {
    private final MessageRepository messageRepository;
    private final MessageGroupRepository messageGroupRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/message")
    public String message(HttpServletResponse response, Account account){
//        Cookie cookie = new Cookie("token", BCrypt.hashpw(account.getUsername(), BCrypt.gensalt()));

//        response.addCookie(cookie);

        return "message/message";
    }

    @PostMapping(value = "/message/api/get_groups", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getConversations(@RequestBody String request){
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = request.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        String token = params.get("token");
        HashMap<String, String> response = new HashMap<>();
        return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/message/api/get_group", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getGroup(@RequestBody String request){
        HashMap<String, String> params = RequestUtil.getParams(request);
        String groupId = params.get("groupId");
        MessageGroup messageGroup = messageGroupRepository.findById(Integer.parseInt(groupId)).get();
        HashMap<String, String> response = new HashMap<>();
        for (Account account : messageGroup.getMembers()) {
            response.put(account.getUsername(), account.getUsername());
        }
        return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK);
    }

    @PostMapping("/message/api/get_messages")
    public String getMessages(Account account) {
        // get all messages for that conversation
        // return json
        return "message/messages";
    }

    @PostMapping("/message/api/send_message")
    public String sendMessage(Account account) {
        // send message to that conversation
        // return json
        return "message/message";
    }

    @PostMapping(value = "/message/api/create_group", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HashMap<String, String>> getGroups(@RequestBody String request){
        HashMap<String, String> params = RequestUtil.getParams(request);
        String token = params.get("token");
        String userIds = params.get("userIds");
        String name = "Generic Group #" + (int) (Math.random() * 100);
        String[] userIdsList = userIds.split("%2C");
        List<Account> users = new ArrayList<Account>();
        for (String userId : userIdsList) {
            users.add(accountRepository.findById(Integer.parseInt(userId)).get());
        }

        MessageGroup messageGroup = new MessageGroup(name, LocalDateTime.now(), users);
        messageGroupRepository.save(messageGroup);

        HashMap<String, String> response = new HashMap<>();
        response.put("groupId", String.valueOf(messageGroup.getId()));
        response.put("name", messageGroup.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
