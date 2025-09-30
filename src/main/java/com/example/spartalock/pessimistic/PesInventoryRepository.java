package com.example.spartalock.pessimistic;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PesInventoryRepository extends JpaRepository<PesInventory, Long> {

    // 비관적 락 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PesInventory p where p.id = :id")
    Optional<PesInventory> findByIdWithPessimisticLock(@Param("id") long id);
}
