package com.webapp.bankingportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FundTransferRequest(
        @NotBlank
        @Pattern(regexp = "\\d{7}", message = "口座番号は7桁の数字で入力してください")
        String targetAccountNumber,
        @NotBlank String pin,
        @Positive(message = "金額は1円以上で入力してください")
        long amount,
        @Size(max = 120, message = "メモは120文字以内で入力してください") String message) {
}
