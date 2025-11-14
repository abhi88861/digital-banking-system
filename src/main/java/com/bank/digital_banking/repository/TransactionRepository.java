package com.bank.digital_banking.repository;

import com.bank.digital_banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
}
