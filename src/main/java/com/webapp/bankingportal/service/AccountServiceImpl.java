package com.webapp.bankingportal.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.TransactionRepository;
import com.webapp.bankingportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.AccountNotFoundException;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.exception.InvalidPinException;
import java.util.Date;
import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.dto.RecipientResponse;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public Account createAccount(User user) {
        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountStatus("Active");
        account.setBalance(0L);
        account.setUser(user);
        return accountRepository.save(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%07d", ThreadLocalRandom.current().nextInt(10_000_000));
        } while (accountRepository.findByAccountNumber(accountNumber) != null);

        return accountNumber;
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("口座が見つかりません");
        }

        return account.getPin() != null;
    }

    private void validatePassword(String accountNumber, String password) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("口座が見つかりません");
        }
        if (password == null || password.isEmpty()) {
            throw new UserInvalidException("パスワードを入力してください");
        }
        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UserInvalidException("パスワードが正しくありません");
        }

    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
        validatePassword(accountNumber, password);

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account.getPin() != null)
            throw new InvalidPinException("暗証番号はすでに登録されています");
        if (pin == null || pin.isEmpty())
            throw new InvalidPinException("暗証番号を入力してください");
        if (!pin.matches("[0-9]{4}"))
            throw new InvalidPinException("暗証番号は4桁の数字で入力してください");

        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    private void validatePin(String accountNumber, String pin) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException("口座が見つかりません");
        }

        if (account.getPin() == null) {
            throw new InvalidPinException("暗証番号が未登録です");
        }
        if (pin == null || pin.isEmpty()) {
            throw new InvalidPinException("暗証番号を入力してください");
        }
        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new InvalidPinException("暗証番号が正しくありません");
        }
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (newPin == null || newPin.isEmpty()) {
            throw new InvalidPinException("新しい暗証番号を入力してください");
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException("新しい暗証番号は4桁の数字で入力してください");
        }

        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }

    private static final long DEPOSIT_MIN_AMOUNT = 1_000;
    private static final long DEPOSIT_MAX_AMOUNT = 1_000_000;
    private static final long WITHDRAW_MIN_AMOUNT = 1_000;
    private static final long WITHDRAW_DAILY_LIMIT = 500_000;
    private static final long TRANSFER_MIN_AMOUNT = 1;
    private static final long TRANSFER_DAILY_LIMIT = 1_000_000;
    private static final long CASH_STEP_AMOUNT = 1_000;
    private static final ZoneId BANKING_ZONE = ZoneId.of("Asia/Bangkok");

    private void validateDepositAmount(long amount) {
        validateMinAmount(amount, DEPOSIT_MIN_AMOUNT);
        validateStepAmount(amount);
        if (amount > DEPOSIT_MAX_AMOUNT) {
            throw new UserInvalidException("入金は1回あたり1,000,000円までです");
        }
    }

    private void validateWithdrawalAmount(String accountNumber, long amount) {
        validateMinAmount(amount, WITHDRAW_MIN_AMOUNT);
        validateStepAmount(amount);
        validateDailyLimit(accountNumber, TransactionType.CASH_WITHDRAWAL, amount, WITHDRAW_DAILY_LIMIT, "出金");
    }

    private void validateTransferAmount(String accountNumber, long amount) {
        validateMinAmount(amount, TRANSFER_MIN_AMOUNT);
        validateDailyLimit(accountNumber, TransactionType.CASH_TRANSFER, amount, TRANSFER_DAILY_LIMIT, "振込");
    }

    private void validateMinAmount(long amount, long minAmount) {
        if (amount < minAmount) {
            throw new UserInvalidException("金額は" + formatAmount(minAmount) + "円以上で入力してください");
        }
    }

    private void validateStepAmount(long amount) {
        if (amount % CASH_STEP_AMOUNT != 0) {
            throw new UserInvalidException("金額は1,000円単位で入力してください");
        }
    }

    private void validateDailyLimit(String accountNumber, TransactionType transactionType, long amount, long dailyLimit, String operationName) {
        LocalDate today = LocalDate.now(BANKING_ZONE);
        Date startDate = Date.from(today.atStartOfDay(BANKING_ZONE).toInstant());
        Date endDate = Date.from(today.plusDays(1).atStartOfDay(BANKING_ZONE).toInstant());
        long usedAmount = transactionRepository.sumAmountBySourceAccountAndTypeBetween(
                accountNumber,
                transactionType,
                startDate,
                endDate);

        if (usedAmount + amount > dailyLimit) {
            throw new UserInvalidException(operationName + "は1日あたり" + formatAmount(dailyLimit) + "円までです");
        }
    }

    private String formatAmount(long amount) {
        return String.format("%,d", amount);
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, long amount) {
        validatePin(accountNumber, pin);
        validateDepositAmount(amount);
        Account account = accountRepository.findByAccountNumber(accountNumber);
        long currentBalance = account.getBalance();
        long newBalance = currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);

        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, long amount) {
        validatePin(accountNumber, pin);
        validateWithdrawalAmount(accountNumber, amount);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        long currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new UserInvalidException("残高が不足しています");
        }
        long newBalance = currentBalance - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);

        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, long amount, String message) {
        validatePin(sourceAccountNumber, pin);
        targetAccountNumber = targetAccountNumber.trim();
        validateTransferAmount(sourceAccountNumber, amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new UserInvalidException("ご自身の口座には振込できません");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);

        if (targetAccount == null) {
            throw new AccountNotFoundException("振込先口座が見つかりません");
        }
        long sourceBalance = sourceAccount.getBalance();

        if (sourceBalance < amount) {
            throw new UserInvalidException("残高が不足しています");
        }
        sourceAccount.setBalance(sourceBalance - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setMessage(message);
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
    }

    @Override
    public RecipientResponse getRecipient(String accountNumber) {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("振込先口座が見つかりません"));

        Account account = user.getAccount();
        return new RecipientResponse(
                account.getAccountNumber(),
                user.getName(),
                account.getBankName() == null ? "ドコモSMTBネット銀行" : account.getBankName(),
                account.getBankCode(),
                account.getBranch() == null || "Ichigo Branch".equals(account.getBranch())
                        ? "イチゴ支店"
                        : account.getBranch(),
                account.getBranchCode(),
                account.getAccountType());
    }

}
