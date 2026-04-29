package com.keyguard.backend.controller;

import com.keyguard.backend.dto.AgentRegistrationRequest;
import com.keyguard.backend.dto.AgentResponse;
import com.keyguard.backend.model.Agent;
import com.keyguard.backend.security.KeyManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agents")
@AllArgsConstructor
public class AgentController {

    private final KeyManagementService keyManagementService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAgent(@RequestBody AgentRegistrationRequest request) {
        try {
            Agent createdAgent = keyManagementService.registerNewAgent(request.getAgentId());
            AgentResponse response = new AgentResponse(
                    createdAgent.getId(),
                    createdAgent.getAgentId(),
                    createdAgent.getHmacSecret(),
                    createdAgent.getAesKey()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        List<AgentResponse> fullAgentList = keyManagementService.getAllAgents()
                .stream()
                .map(agent -> new AgentResponse(
                        agent.getId(),
                        agent.getAgentId(),
                        agent.getHmacSecret(),
                        agent.getAesKey()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(fullAgentList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable Long id) {
        try {
            keyManagementService.deleteLog(id);
            return ResponseEntity.ok("Log entry " + id + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}