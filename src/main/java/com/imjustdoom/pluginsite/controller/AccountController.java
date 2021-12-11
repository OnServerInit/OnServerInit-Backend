package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import com.imjustdoom.pluginsite.dtos.in.CreateAccountRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @GetMapping("/signup")
    public String signup(Model model, @CookieValue(value = "username", defaultValue = "") String username, @RequestParam(name = "error", required = false) String error) {
        model.addAttribute("account", new CreateAccountRequest());
        model.addAttribute("username", username);
        model.addAttribute("error", error);
        return "account/signup";
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletResponse response, @CookieValue(value = "username", defaultValue = "") String username, Model model) {
        model.addAttribute("username", username);
        Cookie cookie = new Cookie("username", null);
        response.addCookie(cookie);
        cookie = new Cookie("id", null);
        response.addCookie(cookie);
        return new RedirectView("/");
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute CreateAccountRequest accountRequest, HttpServletResponse response) {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        if(!pattern.matcher(accountRequest.getUsername()).matches()) return "redirect:/signup?error=invalidcharacter";

        String emailAddress = accountRequest.getEmail();
        String regexPattern = "^(.+)@(\\S+)$";
        boolean validEmail = StringUtil.patternMatches(emailAddress, regexPattern);

        //TODO: check if account exists

        if (!validEmail) return "redirect:/signup?error=invalidemail";

        if (accountRepository.existsByUsernameEqualsIgnoreCase(accountRequest.getUsername())) return "redirect:/signup?error=usernametaken";

        if (accountRepository.existsByEmailEqualsIgnoreCase(emailAddress)) return "redirect:/signup?error=emailtaken";

        Account account = new Account(accountRequest.getUsername(), emailAddress, passwordEncoder.encode(accountRequest.getPassword()));
        accountRepository.save(account);

        Cookie ck = new Cookie("username", account.getUsername());
        response.addCookie(ck);
        ck = new Cookie("id", String.valueOf(account.getId()));
        response.addCookie(ck);

        return "redirect:/profile/" + account.getId();
    }

    @GetMapping("/login")
    public String login(Model model, @CookieValue(value = "username", defaultValue = "") String username, @RequestParam(name = "error", required = false) String error) {
        model.addAttribute("error", error);
        model.addAttribute("account", new CreateAccountRequest());
        model.addAttribute("username", username);
        return "account/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute CreateAccountRequest accountRequest, HttpServletResponse response) {

        if (accountRepository.existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(accountRequest.getUsername(), "")) {
            Optional<Account> account = accountRepository.findByUsernameEqualsIgnoreCase(accountRequest.getUsername());
            if(BCrypt.checkpw(accountRequest.getPassword(), account.get().getPassword())) {
                Cookie ck = new Cookie("username", accountRequest.getUsername());
                response.addCookie(ck);
                ck = new Cookie("id", String.valueOf(account.get().getId()));
                response.addCookie(ck);

                return "redirect:/profile/" + account.get().getId();
            }
            return "redirect:/login?error=incorrectpassword";
        }

        return "redirect:/login?error=notfound";
    }
}