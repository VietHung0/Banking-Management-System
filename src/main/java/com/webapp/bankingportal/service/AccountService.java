package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;

public interface AccountService {

    Account createAccount(User user);

    boolean isPinCreated(String accountNumber);

    void createPin(String accountNumber, String password, String pin);

    void updatePin(String accountNumber, String oldPin, String password, String newPin);


}
