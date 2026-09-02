package com.webapp.bankingportal.service;

import java.util.function.Supplier;

public interface IdempotencyService {

    String execute(String idempotencyKey, String accountNumber, String endpoint, Supplier<String> action);
}
