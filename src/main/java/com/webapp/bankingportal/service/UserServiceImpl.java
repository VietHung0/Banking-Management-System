package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.dto.RegisterRequest;
import com.webapp.bankingportal.dto.UpdateUserRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Lombok tự sinh constructor cho 2 field final này (constructor injection)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Override

    public ResponseEntity<String> registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserInvalidException("Email đã tồn tại");
        }

        if (userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new UserInvalidException("Số điện thoại đã tồn tại");
        }

        User user = new User();
        user.setName(request.name());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setCountryCode(request.countryCode());
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());

        User saveUser = userRepository.save(user);
        Account account = accountService.createAccount(saveUser);
        saveUser.setAccount(account);
        userRepository.save(saveUser);
        return ResponseEntity.ok("Đăng kí thành công");
    }

    @Transactional
    @Override
    public UserResponse updateUser(String accountNumber, UpdateUserRequest request) {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UserInvalidException("Không tìm thấy user"));

        userRepository.findByPhoneNumber(request.phoneNumber())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new UserInvalidException("Số điện thoại đã tồn tại");
                });

        user.setName(request.name());
        user.setCountryCode(request.countryCode());
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());

        return new UserResponse(userRepository.save(user));
    }
}
