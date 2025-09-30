package com.example.spartalock.distributed;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisDisInventoryLockLockManager implements DisInventoryLockManager {

    private final RedissonClient redissonClient;
    private static final String LOCK_KEY_PREFIX = "dis-inventory-lock:";

    @Override
    public void executeWithLock(Long key, Runnable task) throws InterruptedException {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);

        // 10초 내로 락 획득 시도, 15초 대기
        if (lock.tryLock(10, 15, TimeUnit.SECONDS)) {
            try {
                task.run();
            } finally {
                // 현재 스레드가 해당 락을 가지고 있으면 작업을 끝내고 락 해제
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new IllegalStateException("락 획득 실패: " + lockKey);
        }
    }
}
