package com.example.spartalock.optimistic;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptInventoryService {

    private final OptInventoryRepository optInventoryRepository;
    private static final int MAX_RETRY = 5;

    @Transactional
    public OptInventory saveOptInventory(OptInventory optInventory) {
        return optInventoryRepository.save(optInventory);
    }

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = MAX_RETRY,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void incrementCount(Long optInventoryId) {
        OptInventory optInventory = optInventoryRepository.findById(optInventoryId).get();
        optInventory.incrementCount();
    }

    @Transactional(readOnly = true)
    public OptInventory get(Long optInventoryId) {
        return optInventoryRepository.findById(optInventoryId).get();
    }
}
