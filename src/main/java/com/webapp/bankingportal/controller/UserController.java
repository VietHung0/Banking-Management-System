package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // Lombok tự sinh constructor để Spring tiêm UserService vào
    private final UserService userService;

    // 👇 BẠN VIẾT METHOD: registerUser(@Valid @RequestBody User user)
    // trả về ResponseEntity<String>, gọi userService.registerUser(user)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }
}