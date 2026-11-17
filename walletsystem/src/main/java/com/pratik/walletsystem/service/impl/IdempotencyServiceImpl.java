package com.pratik.walletsystem.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratik.walletsystem.entity.IdempotencyRecord;
import com.pratik.walletsystem.enums.IdempotencyStatus;
import com.pratik.walletsystem.exception.IdempotencyConflictException;
import com.pratik.walletsystem.repository.IdempotencyRepository;
import com.pratik.walletsystem.service.IdempotencyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> checkIdempotency(String idempotencyKey, String userEmail, String endpoint, Object requestBody) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        String requestHash = computeHash(requestBody);
        Optional<IdempotencyRecord> recordOpt = idempotencyRepository.findByKeyAndUserEmail(idempotencyKey, userEmail);

        if (recordOpt.isEmpty()) {
            return Optional.empty();
        }

        IdempotencyRecord record = recordOpt.get();

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        if (!record.getRequestHash().equals(requestHash)) {
            throw new IdempotencyConflictException("Idempotency key reuse with different parameters is forbidden.");
        }

        if (record.getStatus() == IdempotencyStatus.PENDING) {
            throw new IdempotencyConflictException("A request with this idempotency key is currently processing.");
        }

        if (record.getStatus() == IdempotencyStatus.COMPLETED) {
            log.info("Idempotent replay for key {} and user {}", idempotencyKey, userEmail);
            return Optional.ofNullable(record.getResponseBody());
        }

        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePending(String idempotencyKey, String userEmail, String endpoint, Object requestBody) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        String requestHash = computeHash(requestBody);
        IdempotencyRecord record = IdempotencyRecord.builder()
                .key(idempotencyKey)
                .userEmail(userEmail)
                .endpoint(endpoint)
                .requestHash(requestHash)
                .status(IdempotencyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        idempotencyRepository.save(record);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(String idempotencyKey, String userEmail, Object responseBody) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        idempotencyRepository.findByKeyAndUserEmail(idempotencyKey, userEmail).ifPresent(record -> {
            try {
                record.setStatus(IdempotencyStatus.COMPLETED);
                record.setResponseBody(objectMapper.writeValueAsString(responseBody));
                idempotencyRepository.save(record);
            } catch (Exception e) {
                log.error("Failed to serialize idempotency response for key {}", idempotencyKey, e);
            }
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(String idempotencyKey, String userEmail, String errorMessage) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        idempotencyRepository.findByKeyAndUserEmail(idempotencyKey, userEmail).ifPresent(record -> {
            record.setStatus(IdempotencyStatus.FAILED);
            record.setResponseBody(errorMessage);
            idempotencyRepository.save(record);
        });
    }

    private String computeHash(Object object) {
        if (object == null) {
            return "";
        }
        try {
            String json = objectMapper.writeValueAsString(object);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error computing hash for idempotency", e);
            return String.valueOf(object.hashCode());
        }
    }
}
