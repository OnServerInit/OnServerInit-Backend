package com.imjustdoom.pluginsite;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.in.account.CreateAccountRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Resource;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.ResourceRepository;
import com.imjustdoom.pluginsite.service.rest.AccountService;
import org.springframework.stereotype.Component;

@Component
public class Test {

    public Test(AccountService accountService, AccountRepository accountRepository, ResourceRepository resourceRepository) throws RestException {
        if (accountRepository.existsByUsernameEqualsIgnoreCase("test"))
            return;
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setEmail("test@test.com");
        createAccountRequest.setUsername("test");
        createAccountRequest.setPassword("test");
        Account account = accountService.register(createAccountRequest);

        Resource resource = new Resource("test", "test", "", "", "", account, "", "none");
        resourceRepository.save(resource);
        accountRepository.save(account);
    }
}
