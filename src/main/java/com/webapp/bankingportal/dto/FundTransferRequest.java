package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record FundTransferRequest(
        @NotBlank String targetAccountNumber,
        @NotBlank String pin,
        @Positive(message = "金額は1円以上で入力してください")
        double amount,
        String message) {
}
