package com.bank.digital_banking.service;

import com.bank.digital_banking.dto.DepositRequest;
import com.bank.digital_banking.dto.TransferRequest;
import com.bank.digital_banking.dto.WithdrawRequest;
import com.bank.digital_banking.entity.Transaction;
import com.bank.digital_banking.entity.User;
import com.bank.digital_banking.repository.TransactionRepository;
import com.bank.digital_banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Deposit: uses transaction_type = 'DEPOSIT' and status = 'COMPLETED' for success.
     */
    @Transactional
    public Transaction deposit(DepositRequest req, String performedByUsername) {
        BigDecimal amount = req.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        // find account owner by accountNumber (users table is source of truth)
        User user = userRepository.findByAccountNumber(req.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + req.getAccountNumber()));

        // authorization: token user must own the account
        if (!user.getUsername().equals(performedByUsername)) {
            throw new RuntimeException("You don't own this account");
        }

        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // update balance
        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        userRepository.save(user);

        // build transaction - use DB-allowed values
        Transaction tx = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(user.getId())
                .accountNumber(user.getAccountNumber())
                .otherAccountNumber(null)
                .amount(amount)
                .balanceAfter(newBalance)
                .currency(user.getCurrency() != null ? user.getCurrency() : "INR")
                .status("COMPLETED")             // DB allowed: PENDING, COMPLETED, FAILED, CANCELLED
                .transactionType("DEPOSIT")      // DB allowed types
                .type("DEPOSIT")                 // legacy/aux field; optional
                .description("Deposit by " + performedByUsername)
                .createdAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(tx);
    }

    /**
     * Withdraw: uses transaction_type = 'WITHDRAWAL' and status = 'COMPLETED' for success.
     */
    @Transactional
    public Transaction withdraw(WithdrawRequest req, String performedByUsername) {
        BigDecimal amount = req.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        User user = userRepository.findByAccountNumber(req.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + req.getAccountNumber()));

        if (!user.getUsername().equals(performedByUsername)) {
            throw new RuntimeException("You don't own this account");
        }

        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = user.getBalance().subtract(amount);
        user.setBalance(newBalance);
        userRepository.save(user);

        Transaction tx = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(user.getId())
                .accountNumber(user.getAccountNumber())
                .otherAccountNumber(null)
                .amount(amount)
                .balanceAfter(newBalance)
                .currency(user.getCurrency() != null ? user.getCurrency() : "INR")
                .status("COMPLETED")
                .transactionType("WITHDRAWAL")   // map to DB-allowed value
                .type("WITHDRAW")
                .description("Withdrawal by " + performedByUsername)
                .createdAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(tx);
    }

    /**
     * Transfer: create two transactions (debit + credit). transaction_type uses 'TRANSFER'.
     * The 'type' field holds IN/OUT semantics (optional).
     */
    @Transactional
    public Transaction transfer(TransferRequest req, String performedByUsername) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        User sender = userRepository.findByAccountNumber(req.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found: " + req.getFromAccountNumber()));

        User receiver = userRepository.findByAccountNumber(req.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found: " + req.getToAccountNumber()));

        if (!sender.getUsername().equals(performedByUsername)) {
            throw new RuntimeException("You don't own the source account");
        }

        if (sender.getAccountStatus() != User.AccountStatus.ACTIVE ||
            receiver.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new RuntimeException("One of the accounts is not active");
        }

        if (sender.getBalance().compareTo(req.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        BigDecimal fromNew = sender.getBalance().subtract(req.getAmount());
        BigDecimal toNew = receiver.getBalance().add(req.getAmount());

        sender.setBalance(fromNew);
        receiver.setBalance(toNew);

        userRepository.save(sender);
        userRepository.save(receiver);

        // debit (sender)
        Transaction debitTx = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(sender.getId())
                .accountNumber(sender.getAccountNumber())
                .otherAccountNumber(receiver.getAccountNumber())
                .amount(req.getAmount())
                .balanceAfter(fromNew)
                .currency("INR")
                .status("COMPLETED")
                .transactionType("TRANSFER")    // DB-allowed
                .type("TRANSFER_OUT")           // auxiliary field to indicate out
                .description("Transfer to " + receiver.getAccountNumber() + " by " + performedByUsername)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(debitTx);

        // credit (receiver)
        Transaction creditTx = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .accountId(receiver.getId())
                .accountNumber(receiver.getAccountNumber())
                .otherAccountNumber(sender.getAccountNumber())
                .amount(req.getAmount())
                .balanceAfter(toNew)
                .currency("INR")
                .status("COMPLETED")
                .transactionType("TRANSFER")    // DB-allowed
                .type("TRANSFER_IN")            // auxiliary field to indicate in
                .description("Transfer from " + sender.getAccountNumber() + " by " + performedByUsername)
                .createdAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(creditTx);
    }

    public List<Transaction> getTransactions(String accountNumber) {
        return transactionRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber);
    }
}
