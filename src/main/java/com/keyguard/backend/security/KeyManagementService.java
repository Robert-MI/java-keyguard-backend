package com.keyguard.backend.security;

import com.keyguard.backend.model.Agent;
import com.keyguard.backend.repository.AgentRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class KeyManagementService {

    private final AgentRepository agentRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public KeyManagementService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public String getHmacSecret(String agentId) {
        Optional<Agent> agent = agentRepository.findByAgentId(agentId);
        return agent.map(Agent::getHmacSecret).orElse(null);
    }

    public Agent registerNewAgent(String agentId) {
        if (agentRepository.findByAgentId(agentId).isPresent()) {
            throw new RuntimeException("Agent ID already exists!");
        }

        Agent newAgent = new Agent();
        newAgent.setAgentId(agentId);
        newAgent.setHmacSecret(generateSecureKey());
        newAgent.setAesKey(generateSecureKey());

        return agentRepository.save(newAgent);
    }

    public String getAesKey(String agentId) {
        Optional<Agent> agent = agentRepository.findByAgentId(agentId);
        return agent.map(Agent::getAesKey).orElse(null);
    }

    private String generateSecureKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder keyBuilder = new StringBuilder(32);

        for (int i = 0; i < 32; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            keyBuilder.append(characters.charAt(randomIndex));
        }

        return keyBuilder.toString();
    }

    public java.util.List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public void deleteLog(Long id) {
        if (agentRepository.existsById(id)) {
            agentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Log not found with id: " + id);
        }
    }
}