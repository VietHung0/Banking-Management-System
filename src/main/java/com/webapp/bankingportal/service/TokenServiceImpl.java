package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getAccount().getAccountNumber())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
