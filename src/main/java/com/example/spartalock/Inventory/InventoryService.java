package com.example.spartalock.Inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void incrementCount(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId).get();
        inventory.incrementCount();
    }

    @Transactional(readOnly = true)
    public Inventory get(Long inventoryId) {
        return inventoryRepository.findById(inventoryId).get();
    }
}
