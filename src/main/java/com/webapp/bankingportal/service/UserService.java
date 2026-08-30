package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;

import com.webapp.bankingportal.dto.RegisterRequest;

public interface UserService {
    ResponseEntity<String> registerUser(RegisterRequest request);
}
