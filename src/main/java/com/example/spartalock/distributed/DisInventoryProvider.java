package com.example.spartalock.distributed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DisInventoryProvider {

    private final DisInventoryRepository disInventoryRepository;

    @Transactional
    public DisInventory save(DisInventory disInventory) {
        return disInventoryRepository.save(disInventory);
    }

    @Transactional
    public void incrementDisInventory(Long disInventoryId) {
        DisInventory disInventory = disInventoryRepository.findById(disInventoryId).get();
        disInventory.incrementCount();
    }

    @Transactional(readOnly = true)
    public DisInventory get(Long disInventoryId) {
        return disInventoryRepository.findById(disInventoryId).get();
    }
}
