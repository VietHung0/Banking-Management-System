package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AmountRequest(
        @NotBlank String pin,
        @Positive(message = "Số tiền phải lớn hơn 0")
        @DecimalMax(value = "10000000", message = "Số tiền không được vượt quá 10000000")
        double amount) {
}
