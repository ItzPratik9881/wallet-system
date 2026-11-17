package com.pratik.walletsystem.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pratik.walletsystem.entity.JournalEntry;
import com.pratik.walletsystem.entity.LedgerAccount;
import com.pratik.walletsystem.entity.LedgerEntry;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.AccountType;
import com.pratik.walletsystem.enums.EntryType;
import com.pratik.walletsystem.enums.TransactionType;
import com.pratik.walletsystem.repository.JournalEntryRepository;
import com.pratik.walletsystem.repository.LedgerAccountRepository;
import com.pratik.walletsystem.service.LedgerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private static final String PLATFORM_SETTLEMENT_CODE = "ACC_PLATFORM_SETTLEMENT";

    private final LedgerAccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;

    @Override
    @Transactional
    public LedgerAccount getOrCreateWalletAccount(Wallet wallet) {
        return accountRepository.findByWallet(wallet)
                .orElseGet(() -> {
                    String code = "ACC_WALLET_" + wallet.getId();
                    return accountRepository.findByAccountCode(code)
                            .orElseGet(() -> {
                                LedgerAccount account = LedgerAccount.builder()
                                        .accountCode(code)
                                        .accountName("User Wallet Account #" + wallet.getId())
                                        .accountType(AccountType.LIABILITY)
                                        .wallet(wallet)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                return accountRepository.save(account);
                            });
                });
    }

    @Override
    @Transactional
    public LedgerAccount getPlatformSettlementAccount() {
        return accountRepository.findByAccountCode(PLATFORM_SETTLEMENT_CODE)
                .orElseGet(() -> {
                    LedgerAccount settlement = LedgerAccount.builder()
                            .accountCode(PLATFORM_SETTLEMENT_CODE)
                            .accountName("Platform Cash Settlement Account")
                            .accountType(AccountType.ASSET)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return accountRepository.save(settlement);
                });
    }

    @Override
    @Transactional
    public JournalEntry recordDeposit(Wallet wallet, BigDecimal amount, String referenceId) {
        LedgerAccount userAccount = getOrCreateWalletAccount(wallet);
        LedgerAccount settlementAccount = getPlatformSettlementAccount();

        JournalEntry journal = JournalEntry.builder()
                .entryNumber("JE-DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .referenceId(referenceId)
                .type(TransactionType.DEPOSIT)
                .description("Deposit into Wallet #" + wallet.getId())
                .entryDate(LocalDateTime.now())
                .build();

        // DEBIT Asset (Cash Settlement), CREDIT Liability (User Wallet)
        LedgerEntry debitEntry = LedgerEntry.builder()
                .journalEntry(journal)
                .account(settlementAccount)
                .entryType(EntryType.DEBIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        LedgerEntry creditEntry = LedgerEntry.builder()
                .journalEntry(journal)
                .account(userAccount)
                .entryType(EntryType.CREDIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        journal.addEntry(debitEntry);
        journal.addEntry(creditEntry);

        log.info("Recorded double-entry ledger deposit: {} into wallet #{}", amount, wallet.getId());
        return journalEntryRepository.save(journal);
    }

    @Override
    @Transactional
    public JournalEntry recordWithdrawal(Wallet wallet, BigDecimal amount, String referenceId) {
        LedgerAccount userAccount = getOrCreateWalletAccount(wallet);
        LedgerAccount settlementAccount = getPlatformSettlementAccount();

        JournalEntry journal = JournalEntry.builder()
                .entryNumber("JE-WTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .referenceId(referenceId)
                .type(TransactionType.WITHDRAW)
                .description("Withdrawal from Wallet #" + wallet.getId())
                .entryDate(LocalDateTime.now())
                .build();

        // DEBIT Liability (User Wallet), CREDIT Asset (Cash Settlement)
        LedgerEntry debitEntry = LedgerEntry.builder()
                .journalEntry(journal)
                .account(userAccount)
                .entryType(EntryType.DEBIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        LedgerEntry creditEntry = LedgerEntry.builder()
                .journalEntry(journal)
                .account(settlementAccount)
                .entryType(EntryType.CREDIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        journal.addEntry(debitEntry);
        journal.addEntry(creditEntry);

        log.info("Recorded double-entry ledger withdrawal: {} from wallet #{}", amount, wallet.getId());
        return journalEntryRepository.save(journal);
    }

    @Override
    @Transactional
    public JournalEntry recordTransfer(Wallet senderWallet, Wallet receiverWallet, BigDecimal amount, String referenceId) {
        LedgerAccount senderAccount = getOrCreateWalletAccount(senderWallet);
        LedgerAccount receiverAccount = getOrCreateWalletAccount(receiverWallet);

        JournalEntry journal = JournalEntry.builder()
                .entryNumber("JE-TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .referenceId(referenceId)
                .type(TransactionType.TRANSFER)
                .description("Transfer from Wallet #" + senderWallet.getId() + " to Wallet #" + receiverWallet.getId())
                .entryDate(LocalDateTime.now())
                .build();

        // DEBIT Sender Liability (Decreases liability to sender)
        LedgerEntry debitEntry = LedgerEntry.builder()
                .journalEntry(journal)
                .account(senderAccount)
                .entryType(EntryType.DEBIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        // CREDIT Receiver Liability (Increases liability to receiver)
        LedgerEntry creditEntry = LedgerEntry.builder()
                .journalEntry(journal)
                .account(receiverAccount)
                .entryType(EntryType.CREDIT)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        journal.addEntry(debitEntry);
        journal.addEntry(creditEntry);

        log.info("Recorded double-entry ledger transfer: {} from wallet #{} to #{}", amount, senderWallet.getId(), receiverWallet.getId());
        return journalEntryRepository.save(journal);
    }
}
