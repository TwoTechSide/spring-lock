package com.example.spartalock.distributed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("redis")
public class DisInventoryServiceTest {

    @Autowired private DisInventoryProvider disInventoryProvider;
    @Autowired private DisInventoryService disInventoryService;
    @Autowired private DisInventoryRepository disInventoryRepository;

    private Long disInventoryId;

    @BeforeEach
    public void setup() {
        disInventoryRepository.deleteAll();
        DisInventory disInventory = new DisInventory();
        disInventoryId = disInventoryProvider.save(disInventory).getId();
        System.out.println(disInventoryId);
    }

    @Test
    @DisplayName("분산 락을 적용하는 경우")
    public void increaseTest() throws InterruptedException {
        int testCnt = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCnt);
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCnt; i++) {
            executorService.submit(() -> {
                try {
                    disInventoryService.incrementDisInventoryWithDistributedLock(disInventoryId);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("락 충돌: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double durationSeconds = duration / 1000.0;

        int finalCount = disInventoryProvider.get(disInventoryId).getCount();
        System.out.println("최종 count: " + finalCount);
        System.out.println("성공 업데이트 수: " + successfulUpdates.get());
        System.out.println("테스트 실행 시간: " + durationSeconds + "초");

        assertEquals(finalCount, successfulUpdates.get());
    }
}
