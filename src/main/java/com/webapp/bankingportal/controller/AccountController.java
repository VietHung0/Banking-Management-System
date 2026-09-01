package com.webapp.bankingportal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.AccountService;
import com.webapp.bankingportal.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinStatusResponse;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.dto.RecipientResponse;
import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.util.LoggedinUser;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/pin/check")
    public ResponseEntity<PinStatusResponse> checkPin() {
        boolean hasPin = accountService.isPinCreated(LoggedinUser.getAccountNumber());
        String message = hasPin ? "PIN đã được tạo" : "PIN chưa được tạo";

        return ResponseEntity.ok(new PinStatusResponse(hasPin, message));
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPin(@Valid @RequestBody PinRequest pinRequest) {
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());
        return ResponseEntity.ok("Tạo PIN thành công");
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePin(@Valid @RequestBody PinUpdateRequest pinUpdateRequest) {
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());
        return ResponseEntity.ok("Đổi PIN thành công");
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@Valid @RequestBody AmountRequest amountRequest) {
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());
        return ResponseEntity.ok("Gửi tiền thành công");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(@Valid @RequestBody AmountRequest amountRequest) {
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());
        return ResponseEntity.ok("Rút tiền thành công");
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@Valid @RequestBody FundTransferRequest fundTransferRequest) {
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount(),
                fundTransferRequest.message());
        return ResponseEntity.ok("Chuyển tiền thành công");
    }

    @GetMapping("/recipient")
    public ResponseEntity<RecipientResponse> getRecipient(@RequestParam String accountNumber) {
        return ResponseEntity.ok(accountService.getRecipient(accountNumber));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionByAccountNumber() {
        return ResponseEntity.ok(
                transactionService.getAllTransactionsByAccountNumber(
                        LoggedinUser.getAccountNumber()));
    }
}
