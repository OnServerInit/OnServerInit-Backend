package com.imjustdoom.pluginsite.dtos.in.account;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateAccountRequest {

    private String username;
    private String email;
    private String password;
}