package com.webapp.bankingportal.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "idempotencyKey", "accountNumber", "endpoint" })
})
@Data
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String idempotencyKey;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String accountNumber;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String endpoint;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String responseMessage;

    @NotNull
    @Column(nullable = false)
    private Date createdAt = new Date();
}
