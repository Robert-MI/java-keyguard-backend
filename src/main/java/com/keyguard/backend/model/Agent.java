package com.keyguard.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "agents")
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String agentId;

    @Column(nullable = false)
    private String hmacSecret;

    @Column(nullable = false)
    private String aesKey;
}