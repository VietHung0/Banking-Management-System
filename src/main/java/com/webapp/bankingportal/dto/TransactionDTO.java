package com.webapp.bankingportal.dto;

import java.util.Date;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private double amount;
    private TransactionType transactionType;
    private Date transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private String message;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.transactionType = transaction.getTransactionType();
        this.transactionDate = transaction.getTransactionDate();
        this.message = transaction.getMessage();
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();
        if (transaction.getTargetAccount() != null) {
            this.targetAccountNumber = transaction.getTargetAccount().getAccountNumber();
        } else {
            this.targetAccountNumber = "-";
        }
    }

}
