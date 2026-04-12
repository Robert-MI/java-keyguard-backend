package com.keyguard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedLogUploadRequest {
    private Long id;
    private String timestamp;
    private String encryptedContext;
    private String encryptedPayload;
}
