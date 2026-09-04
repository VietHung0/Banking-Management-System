package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AmountRequest(
        @NotBlank String pin,
        @Positive(message = "金額は1円以上で入力してください")
        long amount) {
}
