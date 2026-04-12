package com.keyguard.backend.controller;

import com.keyguard.backend.dto.EncryptedLogBatchRequest;
import com.keyguard.backend.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadLogs(@RequestBody EncryptedLogBatchRequest request) {
        logService.saveBatch(request.getRecords());
        return ResponseEntity.ok("Logs received");
    }

    @GetMapping("/raw")
    public ResponseEntity<Map<String, Object>> getRawLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(createPageResponse(logService.getAllRawLogs(pageable)));
    }

    @GetMapping("/decrypted")
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(createPageResponse(logService.getAllLogs(pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable Long id) {
        try {
            logService.deleteLog(id);
            return ResponseEntity.ok("Log entry " + id + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    private Map<String, Object> createPageResponse(Page<?> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("pageSize", page.getSize());
        return response;
    }
}