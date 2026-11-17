package com.pratik.walletsystem.service.impl;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pratik.walletsystem.dto.DashboardResponse;
import com.pratik.walletsystem.dto.DashboardTransactionDto;
import com.pratik.walletsystem.entity.Transaction;
import com.pratik.walletsystem.entity.User;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.TransactionType;
import com.pratik.walletsystem.exception.UserNotFoundException;
import com.pratik.walletsystem.exception.WalletNotFoundException;
import com.pratik.walletsystem.repository.TransactionRepository;
import com.pratik.walletsystem.repository.UserRepository;
import com.pratik.walletsystem.repository.WalletRepository;
import com.pratik.walletsystem.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + email));

        long depositCount = transactionRepository.countByReceiverWalletAndType(
                wallet,
                TransactionType.DEPOSIT);

        long withdrawCount = transactionRepository.countBySenderWalletAndType(
                wallet,
                TransactionType.WITHDRAW);

        long transferCount = transactionRepository.countBySenderWalletAndType(
                wallet,
                TransactionType.TRANSFER);

        long totalTransactions =
                transactionRepository.countBySenderWalletOrReceiverWallet(
                        wallet,
                        wallet);

        BigDecimal totalReceived =
                transactionRepository.getTotalReceived(wallet);

        BigDecimal totalSent =
                transactionRepository.getTotalSent(wallet);

        // Fetch only the top 5 transactions via DB query instead of loading full history
        List<Transaction> recentTxEntities = transactionRepository
                .findTop5BySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet);

        List<DashboardTransactionDto> recentTransactions = recentTxEntities.stream()
                .map(t -> DashboardTransactionDto.builder()
                        .type(t.getType())
                        .status(t.getStatus())
                        .amount(t.getAmount())
                        .createdAt(t.getCreatedAt())
                        .senderWalletId(t.getSenderWallet() == null ? null : t.getSenderWallet().getId())
                        .receiverWalletId(t.getReceiverWallet() == null ? null : t.getReceiverWallet().getId())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .userName(user.getFullName())
                .email(user.getEmail())
                .greeting(getGreeting())
                .walletId(wallet.getId())
                .walletStatus(wallet.getStatus())
                .balance(wallet.getBalance())
                .depositCount((int) depositCount)
                .withdrawCount((int) withdrawCount)
                .transferCount((int) transferCount)
                .totalTransactions((int) totalTransactions)
                .totalMoneyReceived(totalReceived)
                .totalMoneySent(totalSent)
                .walletHealth(getWalletHealth(wallet.getBalance()))
                .recentTransactions(recentTransactions)
                .build();
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour >= 6 && hour < 12) {
            return "Good Morning";
        }
        if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        }
        return "Good Evening";
    }

    private String getWalletHealth(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            return "Empty Wallet";
        }
        if (balance.compareTo(BigDecimal.valueOf(500)) < 0) {
            return "Low Balance";
        }
        return "Healthy Wallet";
    }
}
