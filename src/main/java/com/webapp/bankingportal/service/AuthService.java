package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;

import com.webapp.bankingportal.dto.LoginRequest;

public interface AuthService {

    ResponseEntity<String> login(LoginRequest loginRequest);
}
