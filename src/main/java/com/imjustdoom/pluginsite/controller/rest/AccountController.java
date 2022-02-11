package com.imjustdoom.pluginsite.controller.rest;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.account.CreateAccountRequest;
import com.imjustdoom.pluginsite.dtos.in.account.UpdateAccountRequest;
import com.imjustdoom.pluginsite.dtos.out.account.AccountDto;
import com.imjustdoom.pluginsite.dtos.out.account.SelfAccountDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.rest.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public String signupSubmit(@RequestBody CreateAccountRequest accountRequest) throws RestException {
        Account account = this.accountService.register(accountRequest);

        return ""; // todo return a registration DTO of some sort, probably with a session token
    }

    // todo login with JWT

    @GetMapping("/account/details")
    public SelfAccountDto getSelfAccountDetails(Account account) {
        return SelfAccountDto.fromAccount(account);
    }

    @GetMapping("/account/{id}/details")
    public AccountDto getAccountDetails(@PathVariable int id) throws RestException {
        return AccountDto.fromAccount(this.accountService.getAccount(id));
    }

    @PatchMapping("/account/details")
    public SelfAccountDto updateAccountDetails(Account account, @RequestBody UpdateAccountRequest request,
                                               @RequestParam("profilePicture") MultipartFile file) throws RestException {
        return SelfAccountDto.fromAccount(this.accountService.updateAccountDetails(account, request, file));
    }
}
