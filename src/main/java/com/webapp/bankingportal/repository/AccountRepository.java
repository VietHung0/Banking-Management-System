package com.webapp.bankingportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webapp.bankingportal.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountNumber(String accountNumber);

}
