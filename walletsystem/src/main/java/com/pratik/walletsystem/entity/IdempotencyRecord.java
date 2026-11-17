package com.pratik.walletsystem.entity;

import java.time.LocalDateTime;

import com.pratik.walletsystem.enums.IdempotencyStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "idempotency_records", indexes = {
    @Index(name = "idx_idemp_user_key", columnList = "idempotency_key, user_email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {

    @Id
    @Column(name = "idempotency_key", length = 128, nullable = false)
    private String key;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(nullable = false, length = 128)
    private String endpoint;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private IdempotencyStatus status;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
