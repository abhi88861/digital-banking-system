package com.bank.digital_banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank @Email private String email;
    @NotBlank @Size(min=8) private String password;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String phoneNumber;
    private String dateOfBirth;
    private String address;
    @NotBlank private String role;        // CUSTOMER / MANAGER / ADMIN
    @NotBlank private String accountType; // SAVINGS / CURRENT / FIXED_DEPOSIT
}
