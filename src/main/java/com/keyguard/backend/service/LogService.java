package com.keyguard.backend.service;

import com.keyguard.backend.dto.EncryptedLogUploadRequest;
import com.keyguard.backend.model.EncryptedLogRecord;
import com.keyguard.backend.repository.EncryptedLogRecordRepository;
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
    private final String secret;

    public LogService(EncryptedLogRecordRepository repository,
                      @Value("${encryption.secret}") String secret) {
        this.repository = repository;
        this.secret = secret;
    }

    public void saveBatch(List<EncryptedLogUploadRequest> requests) {

        List<EncryptedLogRecord> records = requests.stream().map(req -> {
            EncryptedLogRecord record = new EncryptedLogRecord();
            record.setAgentLogId(req.getId());
            record.setTimestamp(req.getTimestamp());
            record.setEncryptedContext(req.getEncryptedContext());
            record.setEncryptedPayload(req.getEncryptedPayload());
            return record;
        }).toList();

        repository.saveAll(records);
    }

    public Page<EncryptedLogRecord> getAllRawLogs(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<EncryptedLogUploadRequest> getAllLogs(Pageable pageable) {
        Page<EncryptedLogRecord> recordPage = repository.findAll(pageable);

        return recordPage.map(record -> {
            EncryptedLogUploadRequest dto = new EncryptedLogUploadRequest();
            dto.setId(record.getAgentLogId());
            dto.setTimestamp(record.getTimestamp());
            dto.setEncryptedContext(decrypt(record.getEncryptedContext()));
            dto.setEncryptedPayload(decrypt(record.getEncryptedPayload()));
            return dto;
        });
    }

    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    private SecretKey getKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
    }
}