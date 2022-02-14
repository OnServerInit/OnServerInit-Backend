package com.imjustdoom.pluginsite.service;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.dtos.out.ProfileDto;
import com.imjustdoom.pluginsite.dtos.out.account.AccountDto;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UpdateRepository updateRepository;
    private final AccountService accountService;

    public ProfileDto getProfileDto(int userId) throws RestException {
        Account account = this.accountService.getAccount(userId);
        int totalDownloads = this.updateRepository.getTotalAccountDownloads(userId).orElse(0);

        return new ProfileDto(account.getId(), totalDownloads, AccountDto.fromAccount(account));
    }
}
