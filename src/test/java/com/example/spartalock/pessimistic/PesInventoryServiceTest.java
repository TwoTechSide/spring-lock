package com.example.spartalock.pessimistic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PesInventoryServiceTest {

    @Autowired private PesInventoryRepository pesInventoryRepository;
    @Autowired private PesInventoryService pesInventoryService;

    private Long pesInventoryId;

    @BeforeEach
    public void setup() {
        pesInventoryRepository.deleteAll();
        PesInventory pesInventory = new PesInventory();
        pesInventoryId = pesInventoryService.savePesInventory(pesInventory).getId();
        System.out.println(pesInventoryId);
    }

    @Test
    @DisplayName("비관적 락을 적용하는 경우")
    public void increaseTest() throws InterruptedException {
        int testCnt = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCnt);
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCnt; i++) {
            executorService.submit(() -> {
                try {
                    pesInventoryService.incrementCount(pesInventoryId);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("비관적 락 충돌: " + e.getMessage());
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

        int finalCount = pesInventoryService.get(pesInventoryId).getCount();
        System.out.println("최종 count: " + finalCount);
        System.out.println("성공 업데이트 수: " + successfulUpdates.get());
        System.out.println("테스트 실행 시간: " + durationSeconds + "초");

        assertEquals(finalCount, successfulUpdates.get());
    }
}
