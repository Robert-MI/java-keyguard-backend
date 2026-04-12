package com.keyguard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DecryptedLogResponse {
    private Long id;
    private String timestamp;
    private String context;
    private String payload;
}