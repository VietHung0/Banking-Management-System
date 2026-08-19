package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;

import com.webapp.bankingportal.entity.User;

public interface UserService {
    public ResponseEntity<String> registerUser(User user);
    // 👇 BẠN VIẾT 1 METHOD: registerUser(User user) trả về ResponseEntity<String>
    // (giống bản gốc — chỉ cần 1 method này cho Giai đoạn 2)

}
