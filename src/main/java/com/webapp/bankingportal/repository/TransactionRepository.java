package com.webapp.bankingportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(
            String sourceAccountNumber,
            String targetAccountNumber);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.sourceAccount.accountNumber = :accountNumber
              and t.transactionType = :transactionType
              and t.transactionDate >= :startDate
              and t.transactionDate < :endDate
            """)
    double sumAmountBySourceAccountAndTypeBetween(
            @Param("accountNumber") String accountNumber,
            @Param("transactionType") TransactionType transactionType,
            @Param("startDate") java.util.Date startDate,
            @Param("endDate") java.util.Date endDate);
}
