package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.repositories.BlogRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class HomeController {

    private final BlogRepository blogRepository;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {

        blogRepository.findAll();

        model.addAttribute("auth", authentication);
        return "home";
    }
}