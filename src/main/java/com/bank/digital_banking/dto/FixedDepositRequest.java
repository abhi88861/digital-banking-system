package com.bank.digital_banking.dto;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class FixedDepositRequest {
    @NotNull private String accountNumber;
    @NotNull private BigDecimal amount;
    // optionally add tenure, interest rate etc.
}
