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
    private long balance;
    private String accountType;
    private String bankName;
    private String bankCode;
    private String bankAddress;
    private String branch;
    private String branchCode;
    private String accountStatus;

    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.accountType = account.getAccountType();
        this.bankName = account.getBankName() == null ? "ドコモSMTBネット銀行" : account.getBankName();
        this.bankCode = account.getBankCode() == null ? "0038" : account.getBankCode();
        this.bankAddress = account.getBankAddress() == null ? "東京都港区六本木三丁目2番1号" : account.getBankAddress();
        this.branch = account.getBranch() == null || "Ichigo Branch".equals(account.getBranch())
                ? "イチゴ支店"
                : account.getBranch();
        this.branchCode = account.getBranchCode() == null ? "101" : account.getBranchCode();
        this.accountStatus = account.getAccountStatus() == null ? "Active" : account.getAccountStatus();
    }
}
