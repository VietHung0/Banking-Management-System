package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.LoginResponse;
import com.webapp.bankingportal.entity.Token;
import com.webapp.bankingportal.repository.TokenRepository;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    // tu tao constructor cho cac bien final
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        User user;
        // identifier la mail hoac accountNumber
        if (loginRequest.identifier().contains("@")) {
            user = userRepository.findByEmail(loginRequest.identifier())
                    .orElseThrow(() -> new UserInvalidException("Email không tồn tại"));
        } else {
            user = userRepository.findByAccountAccountNumber(loginRequest.identifier())
                    .orElseThrow(() -> new UserInvalidException("Số tài khoản không tồn tại"));
        }
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new UserInvalidException("Sai mật khẩu");
        }

        String accountNumber = user.getAccount().getAccountNumber();
        String token = jwtUtil.generateToken(accountNumber);
        tokenRepository.save(new Token(token, jwtUtil.extractExpiration(token), user.getAccount()));
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @Override
    public ResponseEntity<String> logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UserInvalidException("Token không hợp lệ");
        }

        String token = authorizationHeader.substring(7);
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserInvalidException("Token không tồn tại"));

        savedToken.setRevoked(true);
        tokenRepository.save(savedToken);

        return ResponseEntity.ok("Đăng xuất thành công");
    }
}
