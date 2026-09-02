package com.webapp.bankingportal.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface TokenService extends UserDetailsService {

    boolean isTokenActive(String token);
}
