package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.exception.RestErrorCode;
import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.account.CreateAccountRequest;
import com.imjustdoom.pluginsite.dtos.in.account.UpdateAccountRequest;
import com.imjustdoom.pluginsite.dtos.out.account.AccountDto;
import com.imjustdoom.pluginsite.dtos.out.account.SelfAccountDto;
import com.imjustdoom.pluginsite.dtos.out.message.MessageGroupDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.AccountService;
import com.imjustdoom.pluginsite.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final MessageService messageService;

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

    @GetMapping("/groups")
    public Page<MessageGroupDto> getMessageGroups(Account account,
                                                  @PageableDefault(size = 25) Pageable pageable) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);

        return this.messageService.getGroups(account, pageable)
            .map(MessageGroupDto::fromMessageGroup);
    }
}
