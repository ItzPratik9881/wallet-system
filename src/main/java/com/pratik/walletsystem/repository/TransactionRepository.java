package com.pratik.walletsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pratik.walletsystem.entity.Transaction;
import com.pratik.walletsystem.entity.Wallet;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderWalletOrReceiverWallet(
            Wallet senderWallet,
            Wallet receiverWallet);

}