package com.bank.digital_banking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private String accountNumber;
    private java.math.BigDecimal amount;
}
