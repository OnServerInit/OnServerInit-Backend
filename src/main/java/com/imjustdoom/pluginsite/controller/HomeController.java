package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.resovlers.AccountArgumentResolver;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Parameter;

@Controller
@AllArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(Account account, Model model) {
        model.addAttribute("account", account);
        return "home";
    }
}