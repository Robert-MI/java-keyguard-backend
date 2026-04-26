package com.keyguard.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyguard.backend.dto.EncryptedLogBatchRequest;
import com.keyguard.backend.security.HmacUtil;
import com.keyguard.backend.service.LogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;
    private final HmacUtil hmacUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/upload")
    public ResponseEntity<String> uploadLogs(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        if (!hmacUtil.isValidSignature(rawPayload, timestamp, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired signature");
        }

        try {
            EncryptedLogBatchRequest request = objectMapper.readValue(rawPayload, EncryptedLogBatchRequest.class);

            logService.saveBatch(request.getRecords());
            return ResponseEntity.ok("Logs received and securely verified");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload format");
        }
    }

    @GetMapping("/raw")
    public ResponseEntity<Map<String, Object>> getRawLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(createPageResponse(logService.getAllRawLogs(pageable)));
    }

    @GetMapping("/decrypted")
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
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

    @DeleteMapping("/bulk")
    public ResponseEntity<String> deleteLogs(@RequestBody List<Long> ids) {
        try {
            logService.deleteLogs(ids);
            return ResponseEntity.ok("Successfully deleted " + ids.size() + " log entries.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
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