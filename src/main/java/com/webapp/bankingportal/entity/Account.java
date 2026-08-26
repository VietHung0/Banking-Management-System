package com.webapp.bankingportal.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Account {

    // 👇 BẠN VIẾT PHẦN NÀY:
    // - id: @Id + @GeneratedValue(IDENTITY)
    // - accountNumber: @NotEmpty + @Column(unique = true)
    // - accountType: @NotEmpty, default "Savings"
    // - accountStatus: String (không annotation)
    // - balance: double
    // - branch: String, default "NIT"
    // - ifscCode: String, default "NIT001"
    // - Pin: String (viết hoa, theo bản gốc)
    // - user: @NotNull @OneToOne @JoinColumn(name="user_id") private User user;
    // - tokens: @OneToMany(mappedBy="account", cascade=CascadeType.ALL) private List<Token> tokens = new ArrayList<>();

}