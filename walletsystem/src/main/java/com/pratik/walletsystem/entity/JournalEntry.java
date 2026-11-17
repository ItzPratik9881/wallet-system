package com.pratik.walletsystem.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pratik.walletsystem.enums.TransactionType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "journal_entries", indexes = {
    @Index(name = "idx_je_ref_id", columnList = "reference_id"),
    @Index(name = "idx_je_entry_date", columnList = "entry_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_number", nullable = false, unique = true, length = 64)
    private String entryNumber;

    @Column(name = "reference_id", length = 64)
    private String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransactionType type;

    @Column(length = 255)
    private String description;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LedgerEntry> entries = new ArrayList<>();

    public void addEntry(LedgerEntry entry) {
        entries.add(entry);
        entry.setJournalEntry(this);
    }
}
