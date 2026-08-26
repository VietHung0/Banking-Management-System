package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.webapp.bankingportal.exception.AccountNotFoundException;
import com.webapp.bankingportal.exception.InvalidPinException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Dữ liệu không hợp lệ");
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        return ResponseEntity.internalServerError().body("Loi server");

    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}