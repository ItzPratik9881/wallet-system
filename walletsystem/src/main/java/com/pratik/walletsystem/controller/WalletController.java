package com.pratik.walletsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pratik.walletsystem.dto.DepositRequest;
import com.pratik.walletsystem.dto.TransferRequest;
import com.pratik.walletsystem.dto.WithdrawRequest;
import com.pratik.walletsystem.service.WalletService;
import com.pratik.walletsystem.dto.TransactionHistoryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @Valid @RequestBody DepositRequest request) {

        String response = walletService.deposit(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
        @Valid @RequestBody WithdrawRequest request) {

        String response = walletService.withdraw(request);

    return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @Valid @RequestBody TransferRequest request) {

        String response = walletService.transfer(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{email}")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable String email) {

        return ResponseEntity.ok(walletService.getTransactionHistory(email));
    }

    @GetMapping("/balance/{email}")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable String email) {

        return ResponseEntity.ok(walletService.getBalance(email));
    }
}