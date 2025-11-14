package com.bank.digital_banking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @Column(name = "transaction_id", length = 36)
    private String transactionId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "other_account_number")
    private String otherAccountNumber;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN, FIXED_DEPOSIT

    @Column(name = "type")
    private String type; // legacy/alternate type column if you keep one

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "status")
    private String status;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (transactionId == null) transactionId = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (currency == null) currency = "INR";
    }
}
