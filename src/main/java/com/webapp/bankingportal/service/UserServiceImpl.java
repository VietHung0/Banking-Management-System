package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.webapp.bankingportal.entity.Account;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Lombok tự sinh constructor cho 2 field final này (constructor injection)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Override

    public ResponseEntity<String> registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserInvalidException("Email đã tồn tại");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saveUser = userRepository.save(user);
        Account account = accountService.createAccount(saveUser);
        saveUser.setAccount(account);
        userRepository.save(saveUser);
        return ResponseEntity.ok("Đăng kí thành công");
    }
}
