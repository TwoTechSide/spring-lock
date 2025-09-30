package com.example.spartalock.Inventory;

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
public class InventoryServiceTest {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private InventoryService inventoryService;

    private Long inventoryId;

    @BeforeEach
    public void setup() {
        inventoryRepository.deleteAll();
        Inventory inventory = new Inventory();
        inventoryId = inventoryService.saveInventory(inventory).getId();
        System.out.println(inventoryId);
    }

    @Test
    @DisplayName("별도의 락 없이 개수를 증가하는 경우")
    public void increaseTest() throws InterruptedException {
        int testCnt = 1000;
        // executor : 여러 요청을 동시에 처리하기 위한 스레드 풀(Thread Pool) 생성
        ExecutorService executor = Executors.newFixedThreadPool(10);
        // 각 스레드마다 latch.countDown();을 한 뒤, latch.await()으로 모든 요청이 끝났는지 확인
        CountDownLatch latch = new CountDownLatch(testCnt);

        // 스레드마다 값이 업데이트 됐음을 확인하는 스레드 정수 카운터
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < testCnt; i++) {
            executor.execute(() -> {
                try {
                    inventoryService.incrementCount(inventoryId);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();    // 스레드 풀 사용 종료

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double durationSeconds = duration / 1000.0;

        int totalCount = inventoryService.get(inventoryId).getCount();
        System.out.println("최종 count: " + totalCount);
        System.out.println("최종 increase: " + successfulUpdates.get());
        System.out.println("테스트 실행 시간: " + durationSeconds + "초");

        assertNotEquals(successfulUpdates.get(), totalCount);
    }
}
