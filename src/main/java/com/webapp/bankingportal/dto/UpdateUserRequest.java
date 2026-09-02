package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String name,
        @NotBlank String countryCode,
        @NotBlank String phoneNumber,
        @NotBlank String address) {
}
