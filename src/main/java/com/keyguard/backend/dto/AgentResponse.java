package com.keyguard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentResponse {
    private Long id;
    private String agentId;
    private String hmacSecret;
    private String aesKey;
}