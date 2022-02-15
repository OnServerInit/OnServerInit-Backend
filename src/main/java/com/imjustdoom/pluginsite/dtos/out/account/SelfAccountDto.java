package com.imjustdoom.pluginsite.dtos.out.account;

import com.imjustdoom.pluginsite.model.Account;

import java.time.LocalDateTime;

public record SelfAccountDto(int id, String username,
                             String email, byte[] profilePicture,
                             LocalDateTime joined) {

    public static SelfAccountDto fromAccount(Account account) {
        return new SelfAccountDto(account.getId(), account.getUsername(),
            account.getEmail(), account.getProfilePicture(),
            account.getJoined());
    }
}
