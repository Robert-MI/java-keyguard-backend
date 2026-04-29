package com.keyguard.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "encrypted_log_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedLogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_log_id")
    private Long agentLogId;

    private String timestamp;

    @Column(name = "encrypted_context", columnDefinition = "TEXT")
    private String encryptedContext;

    @Column(name = "encrypted_payload", columnDefinition = "TEXT")
    private String encryptedPayload;

    @Column(name = "agent_id")
    private String agentId;
}