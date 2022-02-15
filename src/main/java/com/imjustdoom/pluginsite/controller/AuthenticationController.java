package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.config.exception.RestException;
import com.imjustdoom.pluginsite.config.security.jwt.JwtProvider;
import com.imjustdoom.pluginsite.dtos.in.LoginRequest;
import com.imjustdoom.pluginsite.dtos.in.account.CreateAccountRequest;
import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public String register(@RequestBody CreateAccountRequest accountRequest) throws RestException {
        Account account = this.accountService.register(accountRequest);

        return ""; // todo return a registration DTO of some sort, probably with a session token
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        System.out.println("Authenticating " + request.getUsername() + " : " + request.getPassword());
        Authentication authentication = this.authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok()
            .header("token", this.jwtProvider.generateToken(authentication))
            .build();
    }
}
