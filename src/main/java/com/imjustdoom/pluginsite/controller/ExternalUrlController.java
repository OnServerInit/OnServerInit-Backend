package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ExternalUrlController {
    @GetMapping("/redirect")
    public String redirect(Model model, HttpServletResponse response, @RequestParam(name = "url", required = false, value = "") String url, Account account){
        // do you know how to remove the url from the parameters?

        url.replaceAll("%2F", "/");

        Cookie cookie = new Cookie("url", url);

        response.addCookie(cookie);

        model.addAttribute("account", account);

        return "redirect/redirect";
    }
}
