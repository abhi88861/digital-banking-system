package com.bank.digital_banking.repository;

import com.bank.digital_banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // account number helpers (we store account info in users table)
    Optional<User> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
