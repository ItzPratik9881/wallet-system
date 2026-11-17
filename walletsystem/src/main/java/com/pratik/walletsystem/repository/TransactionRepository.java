package com.pratik.walletsystem.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pratik.walletsystem.entity.Transaction;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Rock-solid paginated query with explicit count query
    @Query(value = """
            SELECT t FROM Transaction t
            WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet
            """,
           countQuery = """
            SELECT count(t) FROM Transaction t
            WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet
            """)
    Page<Transaction> findByWallet(@Param("wallet") Wallet wallet, Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.senderWallet = :wallet OR t.receiverWallet = :wallet
            ORDER BY t.createdAt DESC
            """)
    List<Transaction> findAllByWallet(@Param("wallet") Wallet wallet);

    // Optimized top 5 for dashboard
    List<Transaction> findTop5BySenderWalletOrReceiverWalletOrderByCreatedAtDesc(
            Wallet senderWallet,
            Wallet receiverWallet);

    long countBySenderWalletOrReceiverWallet(
            Wallet senderWallet,
            Wallet receiverWallet);

    long countByReceiverWalletAndType(
            Wallet receiverWallet,
            TransactionType type);

    long countBySenderWalletAndType(
            Wallet senderWallet,
            TransactionType type);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.receiverWallet = :wallet
            """)
    BigDecimal getTotalReceived(@Param("wallet") Wallet wallet);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.senderWallet = :wallet
            """)
    BigDecimal getTotalSent(@Param("wallet") Wallet wallet);
}
