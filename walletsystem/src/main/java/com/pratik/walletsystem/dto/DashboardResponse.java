package com.pratik.walletsystem.dto;

import java.math.BigDecimal;
import java.util.List;

import com.pratik.walletsystem.enums.WalletStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    // User Information
    private String userName;
    private String email;

    // Greeting
    private String greeting;

    // Wallet Information
    private Long walletId;
    private WalletStatus walletStatus;
    private BigDecimal balance;

    // Statistics
    private int totalTransactions;
    private int depositCount;
    private int withdrawCount;
    private int transferCount;

    // Financial Summary
    private BigDecimal totalMoneyReceived;
    private BigDecimal totalMoneySent;

    // Dashboard Status
    private String walletHealth;

    // Recent Activity
    private List<DashboardTransactionDto> recentTransactions;

}