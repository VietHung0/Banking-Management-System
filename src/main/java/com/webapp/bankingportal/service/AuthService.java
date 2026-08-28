package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.LoginResponse;

public interface AuthService {

    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);
}
