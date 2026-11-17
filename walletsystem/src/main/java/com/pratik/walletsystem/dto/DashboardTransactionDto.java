package com.pratik.walletsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pratik.walletsystem.enums.TransactionStatus;
import com.pratik.walletsystem.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardTransactionDto {

    private TransactionType type;

    private BigDecimal amount;

    private TransactionStatus status;

    private LocalDateTime createdAt;

    private Long senderWalletId;

    private Long receiverWalletId;

}