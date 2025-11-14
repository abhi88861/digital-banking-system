package com.bank.digital_banking.controller;

import com.bank.digital_banking.dto.DepositRequest;
import com.bank.digital_banking.dto.TransferRequest;
import com.bank.digital_banking.dto.WithdrawRequest;
import com.bank.digital_banking.entity.Transaction;
import com.bank.digital_banking.entity.User;
import com.bank.digital_banking.repository.UserRepository;
import com.bank.digital_banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request, Authentication authentication) {
        try {
            String tokenUsername = authentication.getName();
            User accountOwner = userRepository.findByAccountNumber(request.getAccountNumber())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (!accountOwner.getUsername().equals(tokenUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't own this account");
            }

            Transaction tx = transactionService.deposit(request, tokenUsername);
            return ResponseEntity.ok(tx);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request, Authentication authentication) {
        try {
            String tokenUsername = authentication.getName();
            User accountOwner = userRepository.findByAccountNumber(request.getAccountNumber())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (!accountOwner.getUsername().equals(tokenUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't own this account");
            }

            Transaction tx = transactionService.withdraw(request, tokenUsername);
            return ResponseEntity.ok(tx);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request, Authentication authentication) {
        try {
            String tokenUsername = authentication.getName();
            User fromUser = userRepository.findByAccountNumber(request.getFromAccountNumber())
                    .orElseThrow(() -> new RuntimeException("Source account not found"));
            if (!fromUser.getUsername().equals(tokenUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't own the source account");
            }

            Transaction tx = transactionService.transfer(request, tokenUsername);
            return ResponseEntity.ok(tx);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String accountNumber, Authentication authentication) {
        String tokenUsername = authentication != null ? authentication.getName() : null;
        // Optional: only allow the owner to fetch transactions
        User owner = userRepository.findByAccountNumber(accountNumber).orElse(null);
        if (owner == null) {
            return ResponseEntity.notFound().build();
        }
        if (tokenUsername == null || !owner.getUsername().equals(tokenUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(transactionService.getTransactions(accountNumber));
    }
}
