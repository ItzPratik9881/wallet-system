package com.pratik.walletsystem.service;

import java.math.BigDecimal;

import com.pratik.walletsystem.entity.JournalEntry;
import com.pratik.walletsystem.entity.LedgerAccount;
import com.pratik.walletsystem.entity.Wallet;

public interface LedgerService {

    LedgerAccount getOrCreateWalletAccount(Wallet wallet);

    LedgerAccount getPlatformSettlementAccount();

    JournalEntry recordDeposit(Wallet wallet, BigDecimal amount, String referenceId);

    JournalEntry recordWithdrawal(Wallet wallet, BigDecimal amount, String referenceId);

    JournalEntry recordTransfer(Wallet senderWallet, Wallet receiverWallet, BigDecimal amount, String referenceId);
}
