package com.webapp.bankingportal.service;

import com.webapp.bankingportal.dto.AccountResponse;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.AccountNotFoundException;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public UserResponse getUserDetails(String accountNumber) {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Không tìm thấy user theo tài khoản"));

        return new UserResponse(user);
    }

    @Override
    public AccountResponse getAccountDetails(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản");
        }

        return new AccountResponse(account);
    }
}
