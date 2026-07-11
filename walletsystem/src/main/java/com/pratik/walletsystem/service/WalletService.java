package com.pratik.walletsystem.service;

import java.math.BigDecimal;
import java.util.List;

import com.pratik.walletsystem.dto.DepositRequest;
import com.pratik.walletsystem.dto.TransactionHistoryResponse;
import com.pratik.walletsystem.dto.TransferRequest;
import com.pratik.walletsystem.dto.WithdrawRequest;

public interface WalletService {

    String deposit(DepositRequest request);

    String withdraw(WithdrawRequest request);

    String transfer(TransferRequest request);

    BigDecimal getBalance(String email);

    List<TransactionHistoryResponse> getTransactionHistory(String email);
}