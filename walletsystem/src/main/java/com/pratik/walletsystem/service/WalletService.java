package com.pratik.walletsystem.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.pratik.walletsystem.dto.DepositRequest;
import com.pratik.walletsystem.dto.PagedResponse;
import com.pratik.walletsystem.dto.TransactionHistoryResponse;
import com.pratik.walletsystem.dto.TransferRequest;
import com.pratik.walletsystem.dto.WithdrawRequest;

public interface WalletService {

    TransactionHistoryResponse deposit(String userEmail, DepositRequest request);

    TransactionHistoryResponse withdraw(String userEmail, WithdrawRequest request);

    TransactionHistoryResponse transfer(String senderEmail, TransferRequest request);

    BigDecimal getBalance(String userEmail);

    PagedResponse<TransactionHistoryResponse> getTransactionHistory(String userEmail, Pageable pageable);

    List<TransactionHistoryResponse> getTransactionHistory(String userEmail);

    // Overloads for existing callers
    String deposit(DepositRequest request);

    String withdraw(WithdrawRequest request);

    String transfer(TransferRequest request);
}
