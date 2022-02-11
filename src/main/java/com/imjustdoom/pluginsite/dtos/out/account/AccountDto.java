package com.imjustdoom.pluginsite.dtos.out.account;

import com.imjustdoom.pluginsite.model.Account;

import java.time.LocalDateTime;

public record AccountDto(int id, String username,
                         byte[] profilePicture, LocalDateTime joined) {

    public static AccountDto fromAccount(Account account) {
        return new AccountDto(account.getId(), account.getUsername(),
            account.getProfile_picture(), account.getJoined());
    }
}
