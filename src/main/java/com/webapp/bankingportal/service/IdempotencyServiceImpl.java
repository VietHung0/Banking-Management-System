package com.webapp.bankingportal.service;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.entity.IdempotencyKey;
import com.webapp.bankingportal.repository.IdempotencyKeyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    @Override
    public String execute(String idempotencyKey, String accountNumber, String endpoint, Supplier<String> action) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return action.get();
        }

        return idempotencyKeyRepository
                .findByIdempotencyKeyAndAccountNumberAndEndpoint(idempotencyKey, accountNumber, endpoint)
                .map(IdempotencyKey::getResponseMessage)
                .orElseGet(() -> executeAndSave(idempotencyKey, accountNumber, endpoint, action));
    }

    private String executeAndSave(
            String idempotencyKey,
            String accountNumber,
            String endpoint,
            Supplier<String> action) {
        String responseMessage = action.get();

        IdempotencyKey savedKey = new IdempotencyKey();
        savedKey.setIdempotencyKey(idempotencyKey);
        savedKey.setAccountNumber(accountNumber);
        savedKey.setEndpoint(endpoint);
        savedKey.setResponseMessage(responseMessage);

        idempotencyKeyRepository.save(savedKey);
        return responseMessage;
    }
}
