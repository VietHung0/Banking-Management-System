package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String password,
        @Email @NotBlank String email,
        @NotBlank String countryCode,
        @NotBlank String phoneNumber,
        @NotBlank String address) {
}
