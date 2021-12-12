package com.imjustdoom.pluginsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TermsController {

    @GetMapping("/privacy")
    public String privacy() {
        return "terms/privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms/terms";
    }

    @GetMapping("/contact")
    public String contact() {
        return "terms/contact";
    }
}
