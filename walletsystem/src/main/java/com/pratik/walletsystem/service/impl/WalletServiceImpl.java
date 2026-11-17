package com.pratik.walletsystem.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pratik.walletsystem.dto.DepositRequest;
import com.pratik.walletsystem.dto.PagedResponse;
import com.pratik.walletsystem.dto.TransactionHistoryResponse;
import com.pratik.walletsystem.dto.TransferRequest;
import com.pratik.walletsystem.dto.WithdrawRequest;
import com.pratik.walletsystem.entity.Transaction;
import com.pratik.walletsystem.entity.User;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.TransactionStatus;
import com.pratik.walletsystem.enums.TransactionType;
import com.pratik.walletsystem.enums.WalletStatus;
import com.pratik.walletsystem.exception.InsufficientBalanceException;
import com.pratik.walletsystem.exception.InvalidTransferException;
import com.pratik.walletsystem.exception.UserNotFoundException;
import com.pratik.walletsystem.exception.WalletNotFoundException;
import com.pratik.walletsystem.repository.TransactionRepository;
import com.pratik.walletsystem.repository.UserRepository;
import com.pratik.walletsystem.repository.WalletRepository;
import com.pratik.walletsystem.service.LedgerService;
import com.pratik.walletsystem.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerService ledgerService;

    @Override
    @Transactional
    public TransactionHistoryResponse deposit(String userEmail, DepositRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        // Acquire exclusive pessimistic write lock
        Wallet wallet = walletRepository.findByUserForUpdate(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userEmail));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new InvalidTransferException("Wallet is not active");
        }

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .receiverWallet(wallet)
                .createdAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);

        // Record double-entry ledger postings
        ledgerService.recordDeposit(wallet, request.getAmount(), transaction.getId().toString());

        log.info("Deposit of {} successful for wallet #{}", request.getAmount(), wallet.getId());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionHistoryResponse withdraw(String userEmail, WithdrawRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        // Acquire exclusive pessimistic write lock
        Wallet wallet = walletRepository.findByUserForUpdate(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userEmail));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new InvalidTransferException("Wallet is not active");
        }

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: " + wallet.getBalance());
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

        transaction = transactionRepository.save(transaction);

        // Record double-entry ledger postings
        ledgerService.recordWithdrawal(wallet, request.getAmount(), transaction.getId().toString());

        log.info("Withdrawal of {} successful for wallet #{}", request.getAmount(), wallet.getId());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionHistoryResponse transfer(String senderEmail, TransferRequest request) {
        if (senderEmail.equalsIgnoreCase(request.getReceiverEmail())) {
            throw new InvalidTransferException("Sender and receiver cannot be the same.");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Sender not found with email: " + senderEmail));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new UserNotFoundException("Receiver not found with email: " + request.getReceiverEmail()));

        Wallet unLockedSender = walletRepository.findByUser(sender)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for sender: " + senderEmail));

        Wallet unLockedReceiver = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for receiver: " + request.getReceiverEmail()));

        // Deterministic Lock Ordering to prevent deadlocks: always lock lower ID first!
        Long firstId = Math.min(unLockedSender.getId(), unLockedReceiver.getId());
        Long secondId = Math.max(unLockedSender.getId(), unLockedReceiver.getId());

        Wallet firstLocked = walletRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for ID: " + firstId));
        Wallet secondLocked = walletRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for ID: " + secondId));

        Wallet senderWallet = unLockedSender.getId().equals(firstId) ? firstLocked : secondLocked;
        Wallet receiverWallet = unLockedReceiver.getId().equals(firstId) ? firstLocked : secondLocked;

        if (senderWallet.getStatus() != WalletStatus.ACTIVE || receiverWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new InvalidTransferException("One or both wallets are inactive");
        }

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: " + senderWallet.getBalance());
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

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

        transaction = transactionRepository.save(transaction);

        // Record double-entry ledger postings
        ledgerService.recordTransfer(senderWallet, receiverWallet, request.getAmount(), transaction.getId().toString());

        log.info("Transfer of {} successful from wallet #{} to wallet #{}",
                request.getAmount(), senderWallet.getId(), receiverWallet.getId());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + email));

        return wallet.getBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionHistoryResponse> getTransactionHistory(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + email));

        Page<Transaction> transactions = transactionRepository.findByWallet(wallet, pageable);

        Page<TransactionHistoryResponse> responsePage = transactions.map(this::mapToResponse);
        return PagedResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getTransactionHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + email));

        List<Transaction> transactions = transactionRepository.findAllByWallet(wallet);

        return transactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deposit(DepositRequest request) {
        deposit(request.getEmail(), request);
        return "Amount deposited successfully.";
    }

    @Override
    @Transactional
    public String withdraw(WithdrawRequest request) {
        withdraw(request.getEmail(), request);
        return "Amount withdrawn successfully.";
    }

    @Override
    @Transactional
    public String transfer(TransferRequest request) {
        transfer(request.getSenderEmail(), request);
        return "Amount transferred successfully.";
    }

    private TransactionHistoryResponse mapToResponse(Transaction transaction) {
        return TransactionHistoryResponse.builder()
                .transactionId(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .senderWalletId(transaction.getSenderWallet() != null ? transaction.getSenderWallet().getId() : null)
                .receiverWalletId(transaction.getReceiverWallet() != null ? transaction.getReceiverWallet().getId() : null)
                .build();
    }
}
