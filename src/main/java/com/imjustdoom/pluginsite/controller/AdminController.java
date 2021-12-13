package com.imjustdoom.pluginsite.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class AdminController {

    @GetMapping("/admin")
    public String admin() {
        return "admin/admin";
    }
}
