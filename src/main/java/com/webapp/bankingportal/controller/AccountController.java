package com.webapp.bankingportal.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.AccountService;
import com.webapp.bankingportal.service.IdempotencyService;
import com.webapp.bankingportal.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final IdempotencyService idempotencyService;

    @GetMapping("/pin/check")
    public ResponseEntity<PinStatusResponse> checkPin() {
        boolean hasPin = accountService.isPinCreated(LoggedinUser.getAccountNumber());
        String message = hasPin ? "暗証番号は登録済みです" : "暗証番号は未登録です";

        return ResponseEntity.ok(new PinStatusResponse(hasPin, message));
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPin(@Valid @RequestBody PinRequest pinRequest) {
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());
        return ResponseEntity.ok("暗証番号を登録しました");
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePin(@Valid @RequestBody PinUpdateRequest pinUpdateRequest) {
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());
        return ResponseEntity.ok("暗証番号を変更しました");
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody AmountRequest amountRequest) {
        String accountNumber = LoggedinUser.getAccountNumber();
        String response = idempotencyService.execute(idempotencyKey, accountNumber, "/api/account/deposit", () -> {
            accountService.cashDeposit(
                    accountNumber,
                    amountRequest.pin(),
                    amountRequest.amount());
            return "入金が完了しました";
        });
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody AmountRequest amountRequest) {
        String accountNumber = LoggedinUser.getAccountNumber();
        String response = idempotencyService.execute(idempotencyKey, accountNumber, "/api/account/withdraw", () -> {
            accountService.cashWithdrawal(
                    accountNumber,
                    amountRequest.pin(),
                    amountRequest.amount());
            return "出金が完了しました";
        });
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody FundTransferRequest fundTransferRequest) {
        String accountNumber = LoggedinUser.getAccountNumber();
        String response = idempotencyService.execute(idempotencyKey, accountNumber, "/api/account/fund-transfer", () -> {
            accountService.fundTransfer(
                    accountNumber,
                    fundTransferRequest.targetAccountNumber(),
                    fundTransferRequest.pin(),
                    fundTransferRequest.amount(),
                    fundTransferRequest.message());
            return "振込が完了しました";
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recipient")
    public ResponseEntity<RecipientResponse> getRecipient(@RequestParam String accountNumber) {
        return ResponseEntity.ok(accountService.getRecipient(accountNumber));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionByAccountNumber(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(
                transactionService.getTransactionsByFilter(
                        LoggedinUser.getAccountNumber(),
                        type,
                        fromDate,
                        toDate));
    }
}
