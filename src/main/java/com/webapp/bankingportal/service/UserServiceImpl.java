package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    // 👇 BẠN VIẾT PHẦN NÀY: registerUser(User user) trả về ResponseEntity<String>
    // Logic 4 bước:
    // 1. Kiểm tra trùng email: userRepository.findByEmail(user.getEmail())
    // → nếu có user rồi (isPresent()) thì báo lỗi "Email đã tồn tại"
    // (tạm thời trả ResponseEntity.badRequest()...)
    // 2. Mã hóa password:
    // user.setPassword(passwordEncoder.encode(user.getPassword()))
    // 3. Lưu: userRepository.save(user)
    // 4. Trả về ResponseEntity.ok("Đăng ký thành công")
    public ResponseEntity<String> registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserInvalidException("Email đã tồn tại");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Đăng kí thành công");
    }
}
