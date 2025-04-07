package com.example.brokage.application.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;


@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private String issuer;
    private long expirationMinutes = 30;

    @PostConstruct
    public void initializeSecretKey() {
        if (secretKey == null || secretKey.isEmpty()) {
            secretKey = generateBase64SecretKey();  // Generate if not provided
        }
    }

    private String generateBase64SecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretKeyBytes = new byte[32]; // 256 bits = 32 bytes
        secureRandom.nextBytes(secretKeyBytes);

        // Encode the byte array to Base64 and return as a String
        return Base64.getEncoder().encodeToString(secretKeyBytes);
    }
}