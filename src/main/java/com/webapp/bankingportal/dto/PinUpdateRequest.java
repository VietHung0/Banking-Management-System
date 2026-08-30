package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.NotBlank;

public record PinUpdateRequest(
        @NotBlank String oldPin,
        @NotBlank String newPin,
        @NotBlank String password) {
}
