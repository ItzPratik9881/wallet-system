package com.pratik.walletsystem.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratik.walletsystem.dto.ApiResponse;
import com.pratik.walletsystem.dto.DepositRequest;
import com.pratik.walletsystem.dto.PagedResponse;
import com.pratik.walletsystem.dto.TransactionHistoryResponse;
import com.pratik.walletsystem.dto.TransferRequest;
import com.pratik.walletsystem.dto.WithdrawRequest;
import com.pratik.walletsystem.service.IdempotencyService;
import com.pratik.walletsystem.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet Operations", description = "Endpoints for deposits, withdrawals, transfers, balance, and transaction history")
public class WalletController {

    private final WalletService walletService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Deposits funds into the authenticated user's wallet with concurrency safety, double-entry ledger tracking, and optional idempotency key")
    public ResponseEntity<ApiResponse<TransactionHistoryResponse>> deposit(
            Principal principal,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody DepositRequest request) {

        String userEmail = principal.getName();

        // 1. Check idempotency cache
        Optional<String> cached = idempotencyService.checkIdempotency(idempotencyKey, userEmail, "/api/wallet/deposit", request);
        if (cached.isPresent()) {
            try {
                TransactionHistoryResponse cachedResponse = objectMapper.readValue(cached.get(), TransactionHistoryResponse.class);
                return ResponseEntity.ok(ApiResponse.success(cachedResponse, "Deposit processed successfully (Replayed)"));
            } catch (Exception e) {
                log.warn("Could not deserialize cached response for key {}", idempotencyKey);
            }
        }

        // 2. Mark pending
        idempotencyService.savePending(idempotencyKey, userEmail, "/api/wallet/deposit", request);

        try {
            // 3. Execute
            TransactionHistoryResponse response = walletService.deposit(userEmail, request);
            idempotencyService.complete(idempotencyKey, userEmail, response);
            return new ResponseEntity<>(ApiResponse.success(response, "Deposit completed successfully"), HttpStatus.OK);
        } catch (Exception e) {
            idempotencyService.fail(idempotencyKey, userEmail, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Withdraws funds from the authenticated user's wallet with concurrency safety, double-entry ledger tracking, and optional idempotency key")
    public ResponseEntity<ApiResponse<TransactionHistoryResponse>> withdraw(
            Principal principal,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody WithdrawRequest request) {

        String userEmail = principal.getName();

        // 1. Check idempotency cache
        Optional<String> cached = idempotencyService.checkIdempotency(idempotencyKey, userEmail, "/api/wallet/withdraw", request);
        if (cached.isPresent()) {
            try {
                TransactionHistoryResponse cachedResponse = objectMapper.readValue(cached.get(), TransactionHistoryResponse.class);
                return ResponseEntity.ok(ApiResponse.success(cachedResponse, "Withdrawal processed successfully (Replayed)"));
            } catch (Exception e) {
                log.warn("Could not deserialize cached response for key {}", idempotencyKey);
            }
        }

        // 2. Mark pending
        idempotencyService.savePending(idempotencyKey, userEmail, "/api/wallet/withdraw", request);

        try {
            // 3. Execute
            TransactionHistoryResponse response = walletService.withdraw(userEmail, request);
            idempotencyService.complete(idempotencyKey, userEmail, response);
            return ResponseEntity.ok(ApiResponse.success(response, "Withdrawal completed successfully"));
        } catch (Exception e) {
            idempotencyService.fail(idempotencyKey, userEmail, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds", description = "Transfers funds from authenticated user's wallet to receiver's wallet with deadlock-free locking, double-entry ledger tracking, and optional idempotency key")
    public ResponseEntity<ApiResponse<TransactionHistoryResponse>> transfer(
            Principal principal,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {

        String senderEmail = principal.getName();

        // 1. Check idempotency cache
        Optional<String> cached = idempotencyService.checkIdempotency(idempotencyKey, senderEmail, "/api/wallet/transfer", request);
        if (cached.isPresent()) {
            try {
                TransactionHistoryResponse cachedResponse = objectMapper.readValue(cached.get(), TransactionHistoryResponse.class);
                return ResponseEntity.ok(ApiResponse.success(cachedResponse, "Transfer processed successfully (Replayed)"));
            } catch (Exception e) {
                log.warn("Could not deserialize cached response for key {}", idempotencyKey);
            }
        }

        // 2. Mark pending
        idempotencyService.savePending(idempotencyKey, senderEmail, "/api/wallet/transfer", request);

        try {
            // 3. Execute
            TransactionHistoryResponse response = walletService.transfer(senderEmail, request);
            idempotencyService.complete(idempotencyKey, senderEmail, response);
            return ResponseEntity.ok(ApiResponse.success(response, "Transfer completed successfully"));
        } catch (Exception e) {
            idempotencyService.fail(idempotencyKey, senderEmail, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/balance")
    @Operation(summary = "Get wallet balance", description = "Retrieves the current available balance for the authenticated user's wallet")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(ApiResponse.success(walletService.getBalance(email)));
    }

    // Path variable fallback for balance (secures against IDOR by checking ownership)
    @GetMapping("/balance/{email}")
    @Operation(summary = "Get wallet balance by email", description = "Retrieves wallet balance, ensuring authorization matches the authenticated user")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalanceByEmail(
            Principal principal,
            @PathVariable String email) {
        String authenticatedEmail = principal != null ? principal.getName() : email;
        return ResponseEntity.ok(ApiResponse.success(walletService.getBalance(authenticatedEmail)));
    }

    @GetMapping("/history")
    @Operation(summary = "Get paginated transaction history", description = "Retrieves a paginated list of transaction history for the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionHistoryResponse>>> getTransactionHistory(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        String email = principal.getName();
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ResponseEntity.ok(ApiResponse.success(walletService.getTransactionHistory(email, pageable)));
    }

    // Path variable fallback for transaction history (secures against IDOR)
    @GetMapping("/history/{email}")
    @Operation(summary = "Get transaction history by email", description = "Retrieves transaction history, ensuring authorization matches the authenticated user")
    public ResponseEntity<ApiResponse<List<TransactionHistoryResponse>>> getTransactionHistoryByEmail(
            Principal principal,
            @PathVariable String email) {
        String authenticatedEmail = principal != null ? principal.getName() : email;
        return ResponseEntity.ok(ApiResponse.success(walletService.getTransactionHistory(authenticatedEmail)));
    }
}
