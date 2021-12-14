package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.dtos.in.CreateAccountRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @GetMapping("/signup")
    public String signup(Model model, @RequestParam(name = "error", required = false) String error, Account account, WebRequest request) {
        model.addAttribute("createAccount", new CreateAccountRequest());
        model.addAttribute("account", account);
        model.addAttribute("error", error);
        return "account/signup";
    }

    @GetMapping("/logout")
    public RedirectView logout() {
        return new RedirectView("/");
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute CreateAccountRequest accountRequest, WebRequest request) {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
        if(!pattern.matcher(accountRequest.getUsername()).matches()) return "redirect:/signup?error=invalidcharacter";

        String emailAddress = accountRequest.getEmail();
        String regexPattern = "^(.+)@(\\S+)$";
        boolean validEmail = StringUtil.patternMatches(emailAddress, regexPattern);

        if (!validEmail) return "redirect:/signup?error=invalidemail";

        if (accountRepository.existsByUsernameEqualsIgnoreCase(accountRequest.getUsername())) return "redirect:/signup?error=usernametaken";

        if (accountRepository.existsByEmailEqualsIgnoreCase(emailAddress)) return "redirect:/signup?error=emailtaken";

        Account account = new Account(accountRequest.getUsername(), emailAddress, passwordEncoder.encode(accountRequest.getPassword()));
        accountRepository.save(account);

        return "redirect:/profile/" + account.getId();
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name = "error", required = false) String error, Account account) {
        model.addAttribute("error", error);
        model.addAttribute("createAccount", new CreateAccountRequest());
        model.addAttribute("account", account);
        return "account/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute CreateAccountRequest accountRequest) {

        if (accountRepository.existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(accountRequest.getUsername(), "")) {
            Optional<Account> account = accountRepository.findByUsernameEqualsIgnoreCase(accountRequest.getUsername());
            if(BCrypt.checkpw(accountRequest.getPassword(), account.get().getPassword())) {

                return "redirect:/profile/" + account.get().getId();
            }
            return "redirect:/login?error=incorrectpassword";
        }

        return "redirect:/login?error=notfound";
    }
}