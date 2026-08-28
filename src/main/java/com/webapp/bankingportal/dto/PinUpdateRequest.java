package com.webapp.bankingportal.dto;

public record PinUpdateRequest(String oldPin, String newPin, String password) {
}
