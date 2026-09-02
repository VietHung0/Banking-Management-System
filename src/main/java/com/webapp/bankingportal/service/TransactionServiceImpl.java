package com.webapp.bankingportal.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber) {
        return transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(
                        accountNumber,
                        accountNumber)
                .stream()// duyet tung transaction
                .map(TransactionDTO::new)// doi moi transaction thanh transactionDTO
                .toList();// tra ve list DTo cho controller
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
                .map(TransactionDTO::new)
                .toList();
    }

    private TransactionType parseTransactionType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        return TransactionType.valueOf(type);
    }

    private LocalDate toLocalDate(java.util.Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
