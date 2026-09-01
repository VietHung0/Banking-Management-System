package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private double balance;
    private String accountType;
    private String bankCode;
    private String bankAddress;
    private String branch;
    private String branchCode;
    private String accountStatus;

    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.accountType = account.getAccountType();
        this.bankCode = account.getBankCode() == null ? "0038" : account.getBankCode();
        this.bankAddress = account.getBankAddress() == null ? "Tokyo" : account.getBankAddress();
        this.branch = account.getBranch() == null ? "Ichigo Branch" : account.getBranch();
        this.branchCode = account.getBranchCode() == null ? "101" : account.getBranchCode();
        this.accountStatus = account.getAccountStatus() == null ? "Active" : account.getAccountStatus();
    }
}
