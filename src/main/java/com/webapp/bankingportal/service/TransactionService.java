package com.webapp.bankingportal.service;

import java.time.LocalDate;
import java.util.List;

import com.webapp.bankingportal.dto.TransactionDTO;

public interface TransactionService {

    List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber);

    List<TransactionDTO> getTransactionsByFilter(String accountNumber, String type, LocalDate fromDate, LocalDate toDate);
}
