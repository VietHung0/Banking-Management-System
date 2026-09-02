package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;

import com.webapp.bankingportal.dto.RegisterRequest;
import com.webapp.bankingportal.dto.UpdateUserRequest;
import com.webapp.bankingportal.dto.UserResponse;

public interface UserService {
    ResponseEntity<String> registerUser(RegisterRequest request);

    UserResponse updateUser(String accountNumber, UpdateUserRequest request);
}
