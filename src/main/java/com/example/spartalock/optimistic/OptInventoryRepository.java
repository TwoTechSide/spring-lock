package com.example.spartalock.optimistic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OptInventoryRepository extends JpaRepository<OptInventory, Long> {
}
