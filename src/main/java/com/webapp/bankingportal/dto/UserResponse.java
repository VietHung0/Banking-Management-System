package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String address;
    private String accountNumber;
    private String bankName;
    private String bankCode;
    private String bankAddress;
    private String branchCode;
    private String branch;
    private String accountType;

    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.countryCode = user.getCountryCode();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.accountNumber = user.getAccount().getAccountNumber();
        this.bankName = user.getAccount().getBankName() == null
                ? "ドコモSMTBネット銀行"
                : user.getAccount().getBankName();
        this.bankCode = user.getAccount().getBankCode() == null ? "0038" : user.getAccount().getBankCode();
        this.bankAddress = user.getAccount().getBankAddress() == null
                ? "東京都港区六本木三丁目2番1号"
                : user.getAccount().getBankAddress();
        this.branchCode = user.getAccount().getBranchCode() == null ? "101" : user.getAccount().getBranchCode();
        this.branch = user.getAccount().getBranch() == null || "Ichigo Branch".equals(user.getAccount().getBranch())
                ? "イチゴ支店"
                : user.getAccount().getBranch();
        this.accountType = user.getAccount().getAccountType();
    }
}
