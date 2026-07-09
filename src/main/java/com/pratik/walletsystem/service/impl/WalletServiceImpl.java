package com.pratik.walletsystem.service.impl;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pratik.walletsystem.dto.DepositRequest;
import com.pratik.walletsystem.dto.TransferRequest;
import com.pratik.walletsystem.dto.WithdrawRequest;
import com.pratik.walletsystem.entity.Transaction;
import com.pratik.walletsystem.entity.User;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.TransactionStatus;
import com.pratik.walletsystem.enums.TransactionType;
import com.pratik.walletsystem.repository.TransactionRepository;
import com.pratik.walletsystem.repository.UserRepository;
import com.pratik.walletsystem.repository.WalletRepository;
import com.pratik.walletsystem.service.WalletService;
import com.pratik.walletsystem.dto.TransactionHistoryResponse;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public String deposit(DepositRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .receiverWallet(wallet)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return "Amount deposited successfully.";
    }

    @Override
    @Transactional
    public String withdraw(WithdrawRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .senderWallet(wallet)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return "Amount withdrawn successfully.";
    }

    @Override
    @Transactional
    public String transfer(TransferRequest request) {

        if (request.getSenderEmail().equalsIgnoreCase(request.getReceiverEmail())) {
            throw new RuntimeException("Sender and receiver cannot be the same.");
        }

        User sender = userRepository.findByEmail(request.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        senderWallet.setBalance(
                senderWallet.getBalance().subtract(request.getAmount()));

        receiverWallet.setBalance(
                receiverWallet.getBalance().add(request.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return "Amount transferred successfully.";
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getTransactionHistory(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<Transaction> transactions =
                transactionRepository.findBySenderWalletOrReceiverWallet(
                        wallet, wallet);

        return transactions.stream()
                .map(transaction -> TransactionHistoryResponse.builder()
                        .transactionId(transaction.getId())
                        .type(transaction.getType())
                        .amount(transaction.getAmount())
                        .status(transaction.getStatus())
                        .createdAt(transaction.getCreatedAt())
                        .senderWalletId(
                                transaction.getSenderWallet() != null
                                        ? transaction.getSenderWallet().getId()
                                        : null)
                        .receiverWalletId(
                                transaction.getReceiverWallet() != null
                                        ? transaction.getReceiverWallet().getId()
                                        : null)
                        .build())
                .collect(Collectors.toList());
    }
}