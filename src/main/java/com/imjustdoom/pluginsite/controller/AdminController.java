package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class AdminController {

    private final AccountRepository accountRepository;

    @GetMapping("/admin")
    public String admin(Model model, Account account) {
        model.addAttribute("account", account);
        return "admin/admin";
    }

    @GetMapping("/admin/roles")
    public String roles(Model model, Account account) {
        model.addAttribute("account", account);
        model.addAttribute("username", "");
        model.addAttribute("role", "");
        return "admin/roles";
    }

    @PostMapping("/admin/roles")
    public void setRole(@RequestParam String username, @RequestParam String role, Account account, Model model) {
        model.addAttribute("account", account);

        Optional<Account> optionalAccount = accountRepository.findByUsernameEqualsIgnoreCase(username);
        if(optionalAccount.isEmpty()) return;
        role = role.toUpperCase();
        optionalAccount.get().setRole("ROLE_" + role);
        accountRepository.setRoleById(optionalAccount.get().getId(), "ROLE_" + role);
    }
}