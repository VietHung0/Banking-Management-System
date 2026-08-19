package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.webapp.bankingportal.exception.UserInvalidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 👇 BẠN VIẾT 2 METHOD:

    // 1. handleUserInvalidException(UserInvalidException ex) → trả 400 (badRequest)
    // + ex.getMessage()

    // 2. handleGlobalException(Exception ex) → trả 500 (internalServerError) + lỗi
    // chung
    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        return ResponseEntity.internalServerError().body("Loi server");

    }
}