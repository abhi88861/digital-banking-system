package com.bank.digital_banking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    private String accountNumber;
    private BigDecimal amount;
}
