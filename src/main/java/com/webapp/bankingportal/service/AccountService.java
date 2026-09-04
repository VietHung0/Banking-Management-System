package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.dto.RecipientResponse;

public interface AccountService {

    Account createAccount(User user);

    boolean isPinCreated(String accountNumber);

    void createPin(String accountNumber, String password, String pin);

    void updatePin(String accountNumber, String oldPin, String password, String newPin);

    void cashDeposit(String accountNumber, String pin, long amount);

    void cashWithdrawal(String accountNumber, String pin, long amount);

    void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, long amount, String message);

    RecipientResponse getRecipient(String accountNumber);
}
