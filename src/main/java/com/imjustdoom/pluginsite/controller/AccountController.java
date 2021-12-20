package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import com.imjustdoom.pluginsite.dtos.in.CreateAccountRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final SiteConfig siteConfig;

    @GetMapping("/signup")
    public String signup(Model model, @RequestParam(name = "error", required = false) String error, Account account) {
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
    public String signupSubmit(@ModelAttribute CreateAccountRequest accountRequest) {
        if (!ValidationHelper.isUsernameValid(accountRequest.getUsername())) return "redirect:/signup?error=invalidcharacter";

        String emailAddress = accountRequest.getEmail();

        if (!ValidationHelper.isEmailValid(emailAddress)) return "redirect:/signup?error=invalidemail";

        if (accountRepository.existsByUsernameEqualsIgnoreCase(accountRequest.getUsername()))
            return "redirect:/signup?error=usernametaken";

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

        if (accountRepository.existsByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(accountRequest.getUsername(), "")) { // todo change
            Optional<Account> account = accountRepository.findByUsernameEqualsIgnoreCase(accountRequest.getUsername());
            if (BCrypt.checkpw(accountRequest.getPassword(), account.get().getPassword())) {

                return "redirect:/profile/" + account.get().getId();
            }
            return "redirect:/login?error=incorrectpassword";
        }

        return "redirect:/login?error=notfound";
    }

    @GetMapping("/account/details")
    public String accountDetails(Model model, Account account, @RequestParam(name = "error", required = false) String error) {
        model.addAttribute("account", account);
        model.addAttribute("url", this.siteConfig.getDomain());
        model.addAttribute("error", error);
        return "account/details";
    }

    @PostMapping("/account/details")
    public String postAccountDetails(Account account, @RequestParam String username, @RequestParam String email, @RequestParam String password) {
        if (!ValidationHelper.isUsernameValid(username)) return "redirect:/account/details?error=invalidcharacter";
        if (!ValidationHelper.isEmailValid(email)) return "redirect:/account/details?error=invalidemail";

        if (accountRepository.existsByUsernameEqualsIgnoreCase(username))
            return "redirect:/account/details?error=usernametaken";

        if (accountRepository.existsByEmailEqualsIgnoreCase(email)) return "redirect:/account/details?error=emailtaken";

        accountRepository.setUsernameById(account.getId(), username);
        accountRepository.setEmailById(account.getId(), email);
        accountRepository.setPasswordById(account.getId(), passwordEncoder.encode(password));
        return "redirect:/profile/" + account.getId();
    }
}