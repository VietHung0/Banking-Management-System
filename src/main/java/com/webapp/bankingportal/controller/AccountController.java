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
}
