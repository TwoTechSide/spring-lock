package com.example.spartalock.distributed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DisInventoryService {

    private final DisInventoryProvider disInventoryProvider;
    private final DisInventoryLockManager disInventoryLockManager;

    public void incrementDisInventoryWithDistributedLock(Long disInventoryId) {
        try {
            disInventoryLockManager.executeWithLock(disInventoryId, () ->
                disInventoryProvider.incrementDisInventory(disInventoryId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트 발생: " + disInventoryId, e);
        }
    }
}
