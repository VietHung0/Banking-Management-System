package com.webapp.bankingportal.dto;

public record FundTransferRequest(String targetAccountNumber, String pin, double amount) {
}
