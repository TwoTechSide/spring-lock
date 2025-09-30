package com.example.spartalock.distributed;

public interface DisInventoryLockManager {
    void executeWithLock(Long key, Runnable task) throws InterruptedException;
}
