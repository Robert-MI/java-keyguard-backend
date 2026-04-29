package com.keyguard.backend.service;

import com.keyguard.backend.dto.DecryptedLogResponse;
import com.keyguard.backend.dto.EncryptedLogUploadRequest;
import com.keyguard.backend.model.EncryptedLogRecord;
import com.keyguard.backend.repository.EncryptedLogRecordRepository;
import com.keyguard.backend.security.KeyManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class LogService {
    private final EncryptedLogRecordRepository repository;
    private final KeyManagementService keyManagementService;
    private final String defaultSecret;

    public LogService(EncryptedLogRecordRepository repository,
                      KeyManagementService keyManagementService,
                      @Value("${encryption.secret}") String defaultSecret) {
        this.repository = repository;
        this.keyManagementService = keyManagementService;
        this.defaultSecret = defaultSecret;
    }

    public void saveBatch(List<EncryptedLogUploadRequest> requests, String agentId) {

        List<EncryptedLogRecord> records = requests.stream().map(req -> {
            EncryptedLogRecord record = new EncryptedLogRecord();
            record.setAgentLogId(req.getId());
            record.setTimestamp(req.getTimestamp());
            record.setEncryptedContext(req.getEncryptedContext());
            record.setEncryptedPayload(req.getEncryptedPayload());
            record.setAgentId(agentId);
            return record;
        }).toList();

        repository.saveAll(records);
    }

    public Page<EncryptedLogRecord> getAllRawLogs(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<DecryptedLogResponse> getAllLogs(Pageable pageable) {
        return repository.findAll(pageable).map(record -> {
            String agentKey = null;
            if (record.getAgentId() != null) {
                agentKey = keyManagementService.getAesKey(record.getAgentId());
            }

            return new DecryptedLogResponse(
                    record.getId(),
                    record.getTimestamp(),
                    decrypt(record.getEncryptedContext(), agentKey),
                    decrypt(record.getEncryptedPayload(), agentKey)
            );
        });
    }

    public void deleteLog(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Log not found with id: " + id);
        }
    }

    public void deleteLogs(List<Long> ids) {
        try {
            repository.deleteAllById(ids);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete log batch", e);
        }
    }

    public String decrypt(String encryptedText, String dynamicKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(dynamicKey));

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "[Decryption Failed: Invalid Key]";
        }
    }

    private SecretKey getSecretKey(String dynamicKey) {
        if (dynamicKey == null || dynamicKey.trim().isEmpty()) {
            return new SecretKeySpec(this.defaultSecret.getBytes(StandardCharsets.UTF_8), "AES");
        }
        return new SecretKeySpec(dynamicKey.getBytes(StandardCharsets.UTF_8), "AES");
    }
}