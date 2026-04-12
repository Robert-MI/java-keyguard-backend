package com.keyguard.backend.controller;

import com.keyguard.backend.dto.EncryptedLogBatchRequest;
import com.keyguard.backend.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<?> uploadLogs(@RequestBody EncryptedLogBatchRequest request) {
        logService.saveBatch(request.getRecords());
        return ResponseEntity.ok("Logs received");
    }

    @GetMapping("/raw")
    public ResponseEntity<Map<String, Object>> getRawLogs(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(createPageResponse(logService.getAllRawLogs(pageable)));
    }

    @GetMapping("/decrypted")
    public ResponseEntity<Map<String, Object>> getLogs(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(createPageResponse(logService.getAllLogs(pageable)));
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