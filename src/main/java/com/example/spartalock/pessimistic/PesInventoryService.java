package com.example.spartalock.pessimistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PesInventoryService {

    private final PesInventoryRepository pesInventoryRepository;

    @Transactional
    public PesInventory savePesInventory(PesInventory pesInventory) {
        return pesInventoryRepository.save(pesInventory);
    }

    @Transactional
    public void incrementCount(Long pesInventoryId) {
        PesInventory pesInventory = pesInventoryRepository.findByIdWithPessimisticLock(pesInventoryId).get();
        pesInventory.incrementCount();
    }

    @Transactional(readOnly = true)
    public PesInventory get(Long inventoryId) {
        return pesInventoryRepository.findById(inventoryId).get();
    }
}
