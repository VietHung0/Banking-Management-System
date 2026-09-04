package com.webapp.bankingportal.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final ZoneId BANKING_ZONE = ZoneId.of("Asia/Bangkok");

    private final TransactionRepository transactionRepository;

    @Override
    public List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber) {
        return transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(
                        accountNumber,
                        accountNumber)
                .stream()
                .sorted(Comparator.comparing(transaction -> transaction.getTransactionDate(), Comparator.reverseOrder()))
                .map(transaction -> new TransactionDTO(transaction, accountNumber))
                .toList();
    }

    @Override
    public List<TransactionDTO> getTransactionsByFilter(
            String accountNumber,
            String type,
            LocalDate fromDate,
            LocalDate toDate) {
        TransactionType transactionType = parseTransactionType(type);

        return transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber)
                .stream()
                .filter(transaction -> transactionType == null || transaction.getTransactionType() == transactionType)
                .filter(transaction -> fromDate == null || !toLocalDate(transaction.getTransactionDate()).isBefore(fromDate))
                .filter(transaction -> toDate == null || !toLocalDate(transaction.getTransactionDate()).isAfter(toDate))
                .sorted(Comparator.comparing(transaction -> transaction.getTransactionDate(), Comparator.reverseOrder()))
                .map(transaction -> new TransactionDTO(transaction, accountNumber))
                .toList();
    }

    private TransactionType parseTransactionType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        try {
            return TransactionType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            throw new UserInvalidException("取引種別が正しくありません");
        }
    }

    private LocalDate toLocalDate(java.util.Date date) {
        return date.toInstant()
                .atZone(BANKING_ZONE)
                .toLocalDate();
    }
}
