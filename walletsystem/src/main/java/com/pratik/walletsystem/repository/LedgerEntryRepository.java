package com.pratik.walletsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pratik.walletsystem.entity.LedgerAccount;
import com.pratik.walletsystem.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByAccountOrderByCreatedAtDesc(LedgerAccount account);
}
