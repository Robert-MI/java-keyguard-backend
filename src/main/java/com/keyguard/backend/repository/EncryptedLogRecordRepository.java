package com.keyguard.backend.repository;

import com.keyguard.backend.model.EncryptedLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptedLogRecordRepository extends JpaRepository<EncryptedLogRecord, Long> {
}
