package com.imjustdoom.pluginsite.config.custom;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.time.Duration;

@Getter
@ConfigurationProperties("jwt")
@ConstructorBinding
public class JwtConfig {
    private static final Path KEY_PATH = Path.of("./jwt_secret");

    private final Duration expiryTime;
    private final String domain;
    private final boolean secureCookie;
    private final Key key;

    public JwtConfig(Duration expiryTime, String domain, boolean secureCookie, String key) throws IOException {
        this.expiryTime = expiryTime;
        this.domain = domain;
        this.secureCookie = secureCookie;
        byte[] encodedSecret;
        if (key == null) {
            if (Files.exists(KEY_PATH)) {
                encodedSecret = Files.readAllBytes(KEY_PATH);
            } else {
                encodedSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
                Files.write(KEY_PATH, encodedSecret);
            }
        } else {
            encodedSecret = key.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(encodedSecret);
    }
}
