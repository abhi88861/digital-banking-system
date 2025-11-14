package com.bank.digital_banking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="phone_number", unique = true)
    private String phoneNumber;

    @Column(name="date_of_birth")
    private String dateOfBirth;

    @Column(length = 500)
    private String address;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_type")
    private String accountType; // SAVINGS, CURRENT, FIXED_DEPOSIT

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal balance = java.math.BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "currency")
    private String currency = "INR";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Role { CUSTOMER, MANAGER, ADMIN }
    public enum AccountStatus { ACTIVE, INACTIVE, FROZEN, CLOSED }

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
