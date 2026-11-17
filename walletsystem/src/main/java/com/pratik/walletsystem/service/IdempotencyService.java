package com.pratik.walletsystem.service;

import java.util.Optional;

public interface IdempotencyService {

    Optional<String> checkIdempotency(String idempotencyKey, String userEmail, String endpoint, Object requestBody);

    void savePending(String idempotencyKey, String userEmail, String endpoint, Object requestBody);

    void complete(String idempotencyKey, String userEmail, Object responseBody);

    void fail(String idempotencyKey, String userEmail, String errorMessage);
}
