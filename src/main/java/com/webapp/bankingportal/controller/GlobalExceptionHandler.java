package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.webapp.bankingportal.exception.AccountNotFoundException;
import com.webapp.bankingportal.exception.InvalidPinException;
import com.webapp.bankingportal.exception.UserInvalidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("入力内容が正しくありません");
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnreadableRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("入力内容の形式が正しくありません");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        return ResponseEntity.internalServerError().body("サーバーエラーが発生しました");

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
