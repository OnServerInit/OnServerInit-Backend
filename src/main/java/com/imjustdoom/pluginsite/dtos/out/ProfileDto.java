package com.imjustdoom.pluginsite.dtos.out;

import com.imjustdoom.pluginsite.dtos.out.account.AccountDto;

public record ProfileDto(int id, int totalDownloads,
                         AccountDto accountDto) {

}
