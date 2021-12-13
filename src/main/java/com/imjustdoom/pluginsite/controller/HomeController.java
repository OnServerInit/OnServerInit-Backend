package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(Account account, Model model) {
        model.addAttribute("account", account);
        return "home";
    }
}