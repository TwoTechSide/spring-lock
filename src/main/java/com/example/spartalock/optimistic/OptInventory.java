package com.example.spartalock.optimistic;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class OptInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "int default 0")
    private int count;

    @Version
    private int version;

    public void incrementCount() {
        this.count++;
    }
}
