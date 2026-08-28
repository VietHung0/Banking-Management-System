package com.webapp.bankingportal.controller;

import com.webapp.bankingportal.service.DashboardService;
import com.webapp.bankingportal.util.LoggedinUser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails() {
        return ResponseEntity.ok(dashboardService.getUserDetails(LoggedinUser.getAccountNumber()));
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccountDetails() {
        return ResponseEntity.ok(
                dashboardService.getAccountDetails(
                        LoggedinUser.getAccountNumber()));
    }
}
