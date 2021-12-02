package com.imjustdoom.pluginsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(@CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "id", defaultValue = "") String id, Model model, @CookieValue(value = "id", defaultValue = "") String userId) {
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        model.addAttribute("id", id);
        return "home";
    }
}