package com.example.spartalock.optimistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptInventoryService {

    private final OptInventoryRepository optInventoryRepository;

    @Transactional
    public OptInventory saveOptInventory(OptInventory optInventory) {
        return optInventoryRepository.save(optInventory);
    }

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
