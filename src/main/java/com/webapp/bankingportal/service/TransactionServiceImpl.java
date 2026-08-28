package com.webapp.bankingportal.service;

import java.util.List;

import com.webapp.bankingportal.dto.TransactionDTO;
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
}
