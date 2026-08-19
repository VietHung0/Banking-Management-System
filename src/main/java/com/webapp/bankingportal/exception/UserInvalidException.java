package com.webapp.bankingportal.exception;

public class UserInvalidException extends RuntimeException {

    // 👇 BẠN VIẾT: 1 constructor nhận String message,
    // gọi super(message) để truyền message lên class cha RuntimeException
    public UserInvalidException(String message) {
        super(message);
    }
}