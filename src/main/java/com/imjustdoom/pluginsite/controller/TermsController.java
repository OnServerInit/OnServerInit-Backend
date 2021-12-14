package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TermsController {

    @GetMapping("/privacy")
    public String privacy(Account account, Model model) {
        model.addAttribute("account", account);
        return "terms/privacy";
    }

    @GetMapping("/terms")
    public String terms(Account account, Model model) {
        model.addAttribute("account", account);
        return "terms/terms";
    }

    @GetMapping("/contact")
    public String contact(Account account, Model model) {
        model.addAttribute("account", account);
        return "terms/contact";
    }
}
