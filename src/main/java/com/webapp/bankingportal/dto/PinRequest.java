package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.NotBlank;

public record PinRequest(
        @NotBlank String pin,
        @NotBlank String password) {
}
