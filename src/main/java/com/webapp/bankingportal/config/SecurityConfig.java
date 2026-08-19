package com.webapp.bankingportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // Bean này cung cấp bộ mã hóa mật khẩu BCrypt.
    // Spring sẽ tự tiêm PasswordEncoder vào mọi nơi cần (vd: UserServiceImpl).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
