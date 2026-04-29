package com.keyguard.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class HmacUtil {

    private final KeyManagementService keyManagementService;

    private static final long MAX_REQUEST_AGE_MS = 3600000;

    public boolean isValidSignature(String payload, String timestamp, String providedSignature, String agentId) {
        if (timestamp == null || providedSignature == null || agentId == null) {
            return false;
        }

        String secret = keyManagementService.getHmacSecret(agentId);
        if (secret == null) {
            return false;
        }

        try {
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - requestTime) > MAX_REQUEST_AGE_MS) {
                System.out.println("HMAC validation failed: Request is too old (Replay Attack blocked).");
                return false;
            }

            String dataToSign = payload + timestamp;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(hash);

            return calculatedSignature.equals(providedSignature);

        } catch (Exception e) {
            System.err.println("HMAC validation error: " + e.getMessage());
            return false;
        }
    }
}