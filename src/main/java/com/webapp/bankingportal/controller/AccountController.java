package com.webapp.bankingportal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.util.LoggedinUser;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/pin/check")
    public ResponseEntity<String> checkPin(@RequestParam String accountNumber) {
        boolean isPinCreated = accountService.isPinCreated(accountNumber);

        if (isPinCreated) {
            return ResponseEntity.ok("PIN đã được tạo");
        }
        return ResponseEntity.ok("PIN chưa được tạo");
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPin(@RequestBody PinRequest pinRequest) {
        accountService.createPin(
                pinRequest.accountNumber(),
                pinRequest.password(),
                pinRequest.pin());
        return ResponseEntity.ok("Tạo PIN thành công");
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePin(@RequestBody PinUpdateRequest pinUpdateRequest) {
        accountService.updatePin(
                pinUpdateRequest.accountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());
        return ResponseEntity.ok("Đổi PIN thành công");
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());
        return ResponseEntity.ok("Gửi tiền thành công");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());
        return ResponseEntity.ok("Rút tiền thành công");
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount());
        return ResponseEntity.ok("Chuyển tiền thành công");
    }
}
