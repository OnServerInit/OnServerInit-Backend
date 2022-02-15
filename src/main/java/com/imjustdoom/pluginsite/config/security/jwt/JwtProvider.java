package com.imjustdoom.pluginsite.config.security.jwt;

import com.imjustdoom.pluginsite.config.custom.JwtConfig;
import com.imjustdoom.pluginsite.model.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    public static final String COOKIE_NAME = "JWT_TOKEN";
    private static final String USER_ID = "userId";

    private final JwtConfig jwtConfig;

    public Cookie generateTokenCookie(Authentication authentication) {
        Cookie cookie = new Cookie(COOKIE_NAME, this.generateToken(authentication));
        cookie.setSecure(this.jwtConfig.isSecureCookie());
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) this.jwtConfig.getExpiryTime().getSeconds());
        cookie.setPath("/");
        if (this.jwtConfig.getDomain() != null)
            cookie.setDomain(this.jwtConfig.getDomain());

        return cookie;
    }

    public String generateToken(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();

        Claims claims = Jwts.claims().setSubject(account.getUsername());
        claims.put(USER_ID, account.getId());

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(this.jwtConfig.getExpiryTime())))
            .signWith(this.jwtConfig.getKey())
            .compact();
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(this.jwtConfig.getKey())
            .build()
            .parseClaimsJws(token)
            .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(this.jwtConfig.getKey())
            .build()
            .parseClaimsJws(token) != null;
    }
}
