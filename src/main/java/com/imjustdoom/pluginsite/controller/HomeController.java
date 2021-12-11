package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("auth", authentication);
        return "home";
    }
}