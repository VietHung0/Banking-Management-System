package com.webapp.bankingportal.dto;

public record RecipientResponse(
        String accountNumber,
        String name,
        String bankName,
        String bankCode,
        String branch,
        String branchCode,
        String accountType) {
}
