package com.example.spartalock.optimistic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OptInventoryServiceTest {

    @Autowired OptInventoryRepository optInventoryRepository;
    @Autowired OptInventoryService optInventoryService;

    private Long optInventoryId;

    @BeforeEach
    public void setup() {
        optInventoryRepository.deleteAll();
        OptInventory optInventory = new OptInventory();
        optInventoryId = optInventoryService.saveOptInventory(optInventory).getId();
        System.out.println(optInventoryId);
    }

    @Test
    @DisplayName("낙관적 락을 적용하는 경우")
    public void increaseTest() throws InterruptedException {
        int testCnt = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCnt);
        AtomicInteger exceptionCnt = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCnt; i++) {
            executorService.submit(() -> {
                try {
                    // 재시도 횟수 내에 성공할 경우 break;
                    optInventoryService.incrementCount(optInventoryId);
                } catch (OptimisticLockingFailureException e) {
                    exceptionCnt.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 낙관적 락 테스트 처리 시간 확인
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double durationSeconds = duration / 1000.0;

        int finalCount = optInventoryService.get(optInventoryId).getCount();
        int totalCount = exceptionCnt.get() + finalCount;
        assertEquals(testCnt, totalCount);

        System.out.println("발생 예외 수: " + exceptionCnt.get());
        System.out.println("최종 count: " + finalCount);
        System.out.println("테스트 실행 시간: " + durationSeconds + "초");
        System.out.println("testCount: " + testCnt + ", testedTotalCount: " + totalCount);
    }
}
