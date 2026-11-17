package com.pratik.walletsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pratik.walletsystem.entity.LedgerAccount;
import com.pratik.walletsystem.entity.Wallet;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {

    Optional<LedgerAccount> findByAccountCode(String accountCode);

    Optional<LedgerAccount> findByWallet(Wallet wallet);
}
