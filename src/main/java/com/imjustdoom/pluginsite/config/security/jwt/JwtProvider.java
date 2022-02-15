package com.imjustdoom.pluginsite.config.security.jwt;

import com.imjustdoom.pluginsite.model.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@ConfigurationProperties("jwt")
@ConstructorBinding
public class JwtProvider {
    private static final Path KEY_PATH = Path.of("./jwt_secret");

    private static final String USER_ID = "userId";

    private final Duration expiryTime;
    private final Key key;

    public JwtProvider(String secret, Duration expiryTime) throws IOException {
        byte[] encodedSecret;
        if (secret == null) {
            if (Files.exists(KEY_PATH)) {
                encodedSecret = Files.readAllBytes(KEY_PATH);
            } else {
                encodedSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
                Files.write(KEY_PATH, encodedSecret);
            }
        } else {
            encodedSecret = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(encodedSecret);
        this.expiryTime = expiryTime;
    }

    public String generateToken(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();

        Claims claims = Jwts.claims().setSubject(account.getUsername());
        claims.put(USER_ID, account.getId());

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(this.expiryTime)))
            .signWith(this.key)
            .compact();
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(this.key)
            .build()
            .parseClaimsJws(token)
            .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(this.key)
            .build()
            .parseClaimsJws(token) != null;
    }
}
