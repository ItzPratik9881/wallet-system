package com.pratik.walletsystem.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pratik.walletsystem.enums.EntryType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ledger_entries", indexes = {
    @Index(name = "idx_le_account", columnList = "account_id"),
    @Index(name = "idx_le_journal", columnList = "journal_entry_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private LedgerAccount account;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 16)
    private EntryType entryType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
