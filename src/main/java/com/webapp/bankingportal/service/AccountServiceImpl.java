package com.webapp.bankingportal.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import java.util.UUID;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.AccountNotFoundException;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.exception.InvalidPinException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account createAccount(User user) {
        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);

        return accountNumber;
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản");
        }

        return account.getPin() != null;
    }

    private void validatePassword(String accountNumber, String password) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản");
        }
        if (password == null || password.isEmpty()) {
            throw new UserInvalidException("Mật khẩu không được để trống");
        }
        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UserInvalidException("Mật khẩu không đúng");
        }

    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
        validatePassword(accountNumber, password);

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account.getPin() != null)
            throw new InvalidPinException("PIN đã tồn tại");
        if (pin == null || pin.isEmpty())
            throw new InvalidPinException("PIN không được để trống");
        if (!pin.matches("[0-9]{4}"))
            throw new InvalidPinException("PIN phải gồm đúng 4 chữ số");

        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    private void validatePin(String accountNumber, String pin) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản");
        }

        if (account.getPin() == null) {
            throw new InvalidPinException("Tài khoản chưa tạo PIN");
        }
        if (pin == null || pin.isEmpty()) {
            throw new InvalidPinException("PIN không được để trống");
        }
        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new InvalidPinException("PIN không đúng");
        }
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (newPin == null || newPin.isEmpty()) {
            throw new InvalidPinException("PIN mới không được để trống");
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException("PIN mới phải gồm đúng 4 chữ số");
        }

        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new UserInvalidException("Số tiền phải lớn hơn 0");
        }
        if (amount % 100 != 0) {
            throw new UserInvalidException("Số tiền phải là bội số của 100");
        }
        if (amount > 10000000) {
            throw new UserInvalidException("Số tiền không được vượt quá 10000000");
        }
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);
        Account account = accountRepository.findByAccountNumber(accountNumber);
        double currentBalance = account.getBalance();
        double newBalance = currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        double currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new UserInvalidException("Số dư không đủ");
        }
        double newBalance = currentBalance - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount) {
        validatePin(sourceAccountNumber, pin);
        validateAmount(amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new UserInvalidException("Không thể chuyển tiền cho chính tài khoản của mình");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);

        if (targetAccount == null) {
            throw new AccountNotFoundException("Không tìm thấy tài khoản nhận");
        }
        double sourceBalance = sourceAccount.getBalance();

        if (sourceBalance < amount) {
            throw new UserInvalidException("Số dư không đủ");
        }
        sourceAccount.setBalance(sourceBalance - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }

}
