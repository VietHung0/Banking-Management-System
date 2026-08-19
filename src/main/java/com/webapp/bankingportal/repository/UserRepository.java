package com.webapp.bankingportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 👇 BẠN VIẾT 2 PHƯƠNG THỨC: findByEmail và findByPhoneNumber
    // (khai báo kiểu Optional<User>, giống như bản gốc)
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
    // Optional<User>findbyAccountNumber(String accountNumber);
    // Ghi chú: bản gốc còn có findByAccountAccountNumber(String accountNumber)
    // nhưng phụ thuộc entity Account (chưa tồn tại) → để comment, thêm khi làm Giai
    // đoạn 4.
}
